package com.example.todolist.ui.view

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.TodoListApplication
import com.example.todolist.data.SettingsDataStore
import com.example.todolist.databinding.FragmentTodoListBinding
import com.example.todolist.network.exception.NetworkState
import com.example.todolist.ui.stateholders.TodoItemViewModelFactory
import com.example.todolist.ui.stateholders.TodoItemsViewModel
import com.example.todolist.ui.utils.ItemTouchHelperCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TodoListFragment : Fragment() {

    companion object {
        private const val TAG = "TodoListFragment"
    }

    private var binding: FragmentTodoListBinding? = null

    private var showDoneTasks = true

    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(requireContext())
    }

    private val args: TodoListFragmentArgs by navArgs()

    private val viewModel: TodoItemsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            TodoItemViewModelFactory(
                activity.application,
                (activity.application as TodoListApplication)
                    .getRepositoryComponent()
                    .getRepository()
            )
        )[TodoItemsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentTodoListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TodoListAdapter {
            navigateToEditItemFragment(it.id)
        }
        adapter.onCheckDoneClick = viewModel::changeStatus
        adapter.onDeleteClick = viewModel::deleteItem

        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this.context)
        // display number of done tasks in top app bar
        lifecycleScope.launchWhenStarted {
            viewModel.doneTasks.collectLatest {
                binding?.doneTasks?.text = getString(R.string.tasks_done).format(it)
            }
        }

        // addNewTask button opens EditItemFragment
        binding?.addNewTask?.setOnClickListener {
            navigateToEditItemFragment()
        }

        // newTaskTextview opens EditItemFragment
        binding?.newTaskTextview?.setOnClickListener {
            navigateToEditItemFragment()
        }

        settingsDataStore.preferenceFlow
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { value ->
                showDoneTasks = value
                setIcon(binding?.showDoneTasks)
                chooseTaskList()
            }
            .launchIn(lifecycleScope)

        // remember show/hide done tasks
        binding?.showDoneTasks?.setOnClickListener {
            lifecycleScope.launch {
                settingsDataStore.saveLayoutToPreferenceStore(!showDoneTasks, requireContext())
            }
        }

        // wrap/unwrap top app bar
        binding?.appBarLayout?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding?.doneTasks?.alpha =
                (appBarLayout.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange
        }

        // if created new task scroll to bottom of fragment
        if (args.scrollAllWayDown) {
            binding?.todoListNestedScrollView?.postDelayed(
                {
                    if (!isVisible(binding?.newTaskTextview)) {
                        binding?.appBarLayout?.setExpanded(false)
                    }
                    binding?.todoListNestedScrollView?.fullScroll(View.FOCUS_DOWN)
                },
                200
            )
        }

        // refresh tasks on swipe refresh
        binding?.swiperefresh?.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.refreshItems()
                showRefreshItemsStatus()
                binding?.swiperefresh?.isRefreshing = false
            }
        }

        setItemTouchHelper()
    }

    private fun showRefreshItemsStatus() {
        when (viewModel.lastRefreshStatus) {
            NetworkState.CONNECTION_ERROR -> {
                Toast.makeText(
                    requireContext(),
                    R.string.connection_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
            NetworkState.UNDEFINED_ERROR -> {
                Toast.makeText(
                    requireContext(),
                    R.string.network_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
            NetworkState.OK -> {}
        }
    }

    private fun navigateToEditItemFragment(itemId: Int) {
        val action =
            TodoListFragmentDirections.actionTodoListFragmentToEditItemFragment(itemId)
        this.findNavController().navigate(action)
    }

    private fun navigateToEditItemFragment() {
        navigateToEditItemFragment(-1)
    }

    // check if task is visible on screen
    private fun isVisible(view: View?): Boolean {
        if (view == null) {
            return false
        }
        if (!view.isShown) {
            return false
        }
        val actualPosition = Rect()
        view.getGlobalVisibleRect(actualPosition)
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        return actualPosition.bottom + 100 < screenHeight
    }

    // show all tasks or only undone tasks
    private fun chooseTaskList() {
        val adapter = binding?.recyclerView?.adapter as TodoListAdapter
        viewModel.allItems
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                adapter.submitList(
                    if (showDoneTasks) it else it.filter { item -> !item.isDone }
                )
            }
            .launchIn(lifecycleScope)

    }

    private fun setIcon(button: ImageButton?) {
        button?.setImageDrawable(
            if (showDoneTasks)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_eye)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_eye_off)
        )
    }


    private fun setItemTouchHelper() {
        val swipeButtonWidth = resources.getDimension(R.dimen.swipeButtonWidth).toInt()
        ItemTouchHelper(
            ItemTouchHelperCallback(swipeButtonWidth)
        ).apply {
            attachToRecyclerView(binding?.recyclerView)
        }
    }
}