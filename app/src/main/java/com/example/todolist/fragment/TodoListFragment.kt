package com.example.todolist.fragment

import android.graphics.Canvas
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapter.TodoListAdapter
import com.example.todolist.data.SettingsDataStore
import com.example.todolist.databinding.FragmentTodoListBinding
import com.example.todolist.viewmodel.TodoItemViewModelFactory
import com.example.todolist.viewmodel.TodoItemsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TodoListFragment : Fragment() {

    private var binding: FragmentTodoListBinding? = null

    private var showDoneTasks = true

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: TodoItemsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            TodoItemViewModelFactory(activity.application)
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
            val action =
                TodoListFragmentDirections.actionTodoListFragmentToEditItemFragment(false, it.id)
            this.findNavController().navigate(action)
        }
        adapter.onCheckDoneClick = viewModel::changeStatus
        adapter.onDeleteClick = viewModel::deleteItem

        binding?.recyclerView?.adapter = adapter

        // display number of done tasks in top app bar
        lifecycleScope.launchWhenStarted {
            viewModel.doneTasks.collectLatest {
                binding?.doneTasks?.text = getString(R.string.tasks_done).format(it)
            }
        }

        // add new task button click behavior
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this.context)
        binding?.addNewTask?.setOnClickListener {
            val action = TodoListFragmentDirections.actionTodoListFragmentToEditItemFragment()
            this.findNavController().navigate(action)
        }
        // new task textview click behavior
        binding?.newTaskTextview?.setOnClickListener {
            val action = TodoListFragmentDirections.actionTodoListFragmentToEditItemFragment()
            this.findNavController().navigate(action)
        }

        binding?.showDoneTasks?.setOnClickListener {
            lifecycleScope.launch {
                settingsDataStore.saveLayoutToPreferenceStore(!showDoneTasks, requireContext())
            }
        }

        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.preferenceFlow
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { value ->
                showDoneTasks = value
                setIcon(binding?.showDoneTasks)
                chooseTaskList()

            }
            .launchIn(lifecycleScope)


        // wrap/unwrap top app bar
        binding?.appBarLayout?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

            binding?.doneTasks?.alpha =
                (appBarLayout.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange

        }



        setItemTouchHelper()
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
        ItemTouchHelper(object : ItemTouchHelper.Callback() {


            // limit of swipe, same as delete button width
            private val limitScrollX = resources.getDimension(R.dimen.swipeButtonWidth).toInt()
            private var currentScrollX = 0
            private var currentScrollXWhenInActive = 0
            private var initXWhenInActive = 0f
            private var firstInActive = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    if (dX == 0f) {
                        currentScrollX = viewHolder.itemView.scrollX
                        firstInActive = true
                    }

                    if (isCurrentlyActive) {
                        // swipe with finger

                        var scrollOffset = currentScrollX + (-dX).toInt()
                        // limit swipe
                        if (scrollOffset > limitScrollX) {
                            // swipe left
                            scrollOffset = limitScrollX
                        } else if (scrollOffset < -limitScrollX) {
                            // swipe right
                            scrollOffset = -limitScrollX
                        }

                        viewHolder.itemView.scrollTo(scrollOffset, 0)

                    } else {
                        // swipe with auto animation
                        if (firstInActive) {
                            firstInActive = false
                            currentScrollXWhenInActive = viewHolder.itemView.scrollX
                            initXWhenInActive = dX
                        }

                        if (viewHolder.itemView.scrollX in 1 until limitScrollX) {
                            viewHolder.itemView.scrollTo(
                                (currentScrollXWhenInActive * dX / initXWhenInActive).toInt(),
                                0
                            )
                        } else
                            if (viewHolder.itemView.scrollX in -limitScrollX + 1 until -1) {
                                viewHolder.itemView.scrollTo(
                                    (currentScrollXWhenInActive * dX / initXWhenInActive).toInt(),
                                    0
                                )
                            }
                    }
                }
            }


            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (viewHolder.itemView.scrollX > limitScrollX) {
                    viewHolder.itemView.scrollTo(limitScrollX, 0)
                } else if (viewHolder.itemView.scrollX < -limitScrollX) {
                    viewHolder.itemView.scrollTo(-limitScrollX, 0)
                } else if ((-limitScrollX < viewHolder.itemView.scrollX) && (viewHolder.itemView.scrollX < limitScrollX)) {
                    viewHolder.itemView.scrollTo(0, 0)
                }
            }
        }).apply {
            attachToRecyclerView(binding?.recyclerView)
        }
    }
}