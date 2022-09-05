package com.example.todolist.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.R
import com.example.todolist.data.CommonDateFormats
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.databinding.FragmentEditItemBinding
import com.example.todolist.viewmodel.TodoItemsViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class EditItemFragment : Fragment() {

    private var binding: FragmentEditItemBinding? = null

    private val viewModel: TodoItemsViewModel by activityViewModels()

    private val args: EditItemFragmentArgs by navArgs()

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
                creatingDatePickerDialog()
            } else {
                binding?.pickedDate?.visibility = View.GONE
                binding?.pickedDate?.text = ""
            }
        }

        binding?.topAppBar?.setNavigationOnClickListener {
            val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment()
            this.findNavController().navigate(action)
        }

//        binding?.topAppBar?.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.save_task -> {
//                    addNewItem()
//                    true
//                }
//                else -> false
//            }
//        }
        binding?.delete?.isEnabled = !args.addNewItem
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
        if (args.addNewItem) {
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
        }
        val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment()
        this.findNavController().navigate(action)
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
        if (!args.addNewItem) {
            lifecycleScope.launchWhenStarted {
                viewModel.retrieveItem(args.itemId)?.collectLatest { selectedItem ->
                    item = selectedItem
                    bind(item)
                    setupTopAppBarMenu(::updateItem)
                }
            }
        } else {
            setupTopAppBarMenu(::addNewItem)
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
//                TODO("Turn on deadline switch")
            }
            delete.setOnClickListener {
                deleteItem(itemToDisplay.id)
                val action = EditItemFragmentDirections.actionEditItemFragmentToTodoListFragment()
                this@EditItemFragment.findNavController().navigate(action)
            }
        }
    }

    private fun deleteItem(itemId: String) {
        viewModel.deleteItem(itemId)
    }

    @SuppressLint("SimpleDateFormat")
    private fun creatingDatePickerDialog() {
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
                val dat = formatter.parse("$dayOfMonth.$monthOfYear.$year")?.time
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