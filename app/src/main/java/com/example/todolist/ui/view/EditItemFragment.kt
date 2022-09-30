package com.example.todolist.ui.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.R
import com.example.todolist.data.CommonDateFormats
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.databinding.FragmentEditItemBinding
import com.example.todolist.ui.stateholders.TodoItemViewModelFactory
import com.example.todolist.ui.stateholders.TodoItemsViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class EditItemFragment : Fragment() {

    companion object {
        private const val TAG = "EditItemFragment"
    }

    private var binding: FragmentEditItemBinding? = null

    private val viewModel: TodoItemsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            TodoItemViewModelFactory(activity.application)
        )[TodoItemsViewModel::class.java]
    }

    private val args: EditItemFragmentArgs by navArgs()

    private val isNewTask
        get() = args.itemId == -1

    private lateinit var item: TodoItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentEditItemBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        determineEditOrAddItem()

        binding?.datePickerSwitch?.setOnCheckedChangeListener { _, b ->
            if (b) {
                // if pickedDate not empty let enable switch without creating date picker dialog
                if (binding?.pickedDate?.text == "") {
                    creatingDatePickerDialog()
                }
            } else {
                binding?.pickedDate?.visibility = View.GONE
                binding?.pickedDate?.text = ""
            }
        }

        binding?.topAppBar?.setNavigationOnClickListener {
            val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment()
            this.findNavController().navigate(action)
        }

        binding?.delete?.isEnabled = !isNewTask

        binding?.delete?.setOnClickListener {
            Log.d(TAG, "before")
            val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment()
            Log.d(TAG, "right before")
            deleteItem(item.id)
            Log.d(TAG, "right after")
            this@EditItemFragment.findNavController().navigate(action)
            Log.d(TAG, "after")
        }

    }

    private fun setupTopAppBarMenu(saveTaskResponse: () -> Unit) {
        binding?.topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_task -> {
                    saveTaskResponse()
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addNewItem() {
        if (isNewTask) {
            binding?.let {
                val deadlineDate: Long? = getDeadlineDate()
                viewModel.addItem(
                    it.description.text?.toString() ?: "",
                    getTaskPriority(it.priority.selectedItem.toString()),
                    false,
                    deadlineDate,
                    Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().timeInMillis
                )
            }
            val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment(true)
            this.findNavController().navigate(action)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDeadlineDate(): Long? {
        var deadlineDate: Long? = null
        binding?.let {

            try {
                deadlineDate = SimpleDateFormat(CommonDateFormats.SHORT_DATE)
                    .parse(it.pickedDate.text.toString())?.time
            } catch (e: ParseException) {
            }
        }
        return deadlineDate
    }

    private fun getTaskPriority(priority: String): TaskPriority {
        val prioritiesFromXml = resources.getStringArray(R.array.priority_array).toSet()
        val priorities = listOf("None", "Low", "!! Urgency")
        if (priorities.toSet() != prioritiesFromXml)
            throw IllegalArgumentException("Priorities from xml not match priorities from code")
        return when (priority) {
            priorities[1] -> TaskPriority.LOW
            priorities[2] -> TaskPriority.URGENT
            else -> TaskPriority.NORMAL
        }
    }

    private fun setTaskPriority(priority: TaskPriority): Int {
        val prioritiesFromXml = resources.getStringArray(R.array.priority_array).toSet()
        val priorities = listOf("None", "Low", "!! Urgency")
        if (priorities.toSet() != prioritiesFromXml)
            throw IllegalArgumentException("Priorities from xml not match priorities from code")
        return when (priority) {
            TaskPriority.LOW -> 1
            TaskPriority.URGENT -> 2
            else -> 0
        }
    }

    private fun determineEditOrAddItem() {
        val receivedItem = viewModel.retrieveItem(args.itemId)
        if (!isNewTask && receivedItem != null) {
            item = receivedItem
            bind(item)
            setupTopAppBarMenu(::updateItem)
        } else {
            setupTopAppBarMenu(::addNewItem)
            // set focus on editText
            binding?.description?.requestFocus()
            // show keyboard
            val imm: InputMethodManager? =
                getSystemService(requireContext(), InputMethodManager::class.java)
            imm?.showSoftInput(binding?.description, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun updateItem() {
        binding?.let {
            viewModel.updateItem(
                item.id,
                it.description.text.toString(),
                getTaskPriority(it.priority.selectedItem.toString()),
                item.isDone,
                getDeadlineDate(),
                item.creationDate,
                Calendar.getInstance().timeInMillis
            )
        }
        val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment()
        this.findNavController().navigate(action)
    }

    private fun bind(itemToDisplay: TodoItem) {
        binding?.apply {
            description.setText(itemToDisplay.description)
            priority.setSelection(setTaskPriority(itemToDisplay.priority))
            if (itemToDisplay.deadlineDate != null) {
                pickedDate.setText(
                    CommonDateFormats.msecToDate(
                        itemToDisplay.deadlineDate,
                        CommonDateFormats.SHORT_DATE
                    )
                )
                datePickerSwitch.isChecked = true
                Log.d(TAG, "enabling datePickerSwitch")
                pickedDate.visibility = View.VISIBLE
            }

        }
    }

    private fun deleteItem(itemId: Int) {
        item = TodoItem(0, "", TaskPriority.NORMAL, false, null, 0, 0)
        viewModel.deleteItem(itemId)
    }

    @SuppressLint("SimpleDateFormat")
    private fun creatingDatePickerDialog() {
        Log.d(TAG, "creatingDatePickerDialog")
        // on below line we are getting
        // the instance of our calendar.
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            R.style.MyDatePickerStyle,
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our edit text.
                val formatter = SimpleDateFormat(CommonDateFormats.DIGIT_DATE)
                val dat = formatter.parse("$dayOfMonth.${monthOfYear + 1}.$year")?.time
                dat?.let {
                    binding?.pickedDate?.text =
                        CommonDateFormats.msecToDate(it, CommonDateFormats.SHORT_DATE)
                    binding?.pickedDate?.visibility = View.VISIBLE
                }
            },
            year,
            month,
            day
        )
        // at last we are calling show
        // to display our date picker dialog.
        datePickerDialog.setOnCancelListener {
            binding?.datePickerSwitch?.isChecked = false
        }
        val baseLayout = datePickerDialog.datePicker.getChildAt(0) as LinearLayout
        val childLayout = baseLayout.getChildAt(0) as LinearLayout

        val titleLayout = childLayout.getChildAt(0) as LinearLayout

        val dayText = titleLayout.getChildAt(1) as TextView
        dayText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32f)

        val yearText = titleLayout.getChildAt(0) as TextView
        yearText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        datePickerDialog.show()
    }
}