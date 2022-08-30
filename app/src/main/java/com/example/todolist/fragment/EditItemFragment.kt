package com.example.todolist.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.todolist.R
import com.example.todolist.data.CommonDateFormats
import com.example.todolist.databinding.FragmentEditItemBinding
import com.example.todolist.viewmodel.TodoItemsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class EditItemFragment : Fragment() {

    private var binding: FragmentEditItemBinding? = null

    private val viewModel: TodoItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentEditItemBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.datePickerSwitch?.setOnCheckedChangeListener { compoundButton, b ->
            when (b) {
                true -> {
//                    val datePicker =
//                        MaterialDatePicker.Builder.datePicker()
//                            .setTitleText("Select date")
//                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                            .build()
//
//                    datePicker.show(childFragmentManager, "datePicker")
//
//                    datePicker.addOnPositiveButtonClickListener {
//
//                        val date = Date(it)
//                        val dateString =
//                            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(date)
//                                .lowercase()
//                        binding?.pickedDate?.text = dateString
//                        binding?.pickedDate?.visibility = View.VISIBLE
//                    }
//                    datePicker.addOnNegativeButtonClickListener {
//                        compoundButton.isChecked = false
//                    }
//                    datePicker.addOnCancelListener {
//                        compoundButton.isChecked = false
//                    }


                    // on below line we are getting
                    // the instance of our calendar.
                    val c = Calendar.getInstance()

                    // on below line we are getting
                    // our day, month and year.
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

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
                                binding?.pickedDate?.text = CommonDateFormats.msecToDate(dat, CommonDateFormats.SHORT_DATE)
                                binding?.pickedDate?.visibility = View.VISIBLE
                            }
                        },
                        // on below line we are passing year, month
                        // and day for the selected date in our date picker.
                        year,
                        month,
                        day
                    )
                    // at last we are calling show
                    // to display our date picker dialog.
                    datePickerDialog.setOnCancelListener {
                        compoundButton.isChecked = false
                    }
                    val baseLayout = datePickerDialog.datePicker.getChildAt(0) as LinearLayout
                    val childLayout = baseLayout.getChildAt(0) as LinearLayout

                    val titleLayout = childLayout.getChildAt(0) as LinearLayout

                    val dayText = titleLayout.getChildAt(1) as TextView
                    dayText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32f)
//                    dayText.typeface = ResourcesCompat.getFont(activity, R.font.yourFont)

                    val yearText = titleLayout.getChildAt(0) as TextView
                    yearText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
//                    yearText.typeface = ResourcesCompat.getFont(activity, R.font.yourFont)
                    datePickerDialog.show()

                }
                else -> {
                    binding?.pickedDate?.visibility = View.GONE
                }
            }
        }

//        val spinner = binding?.spinner
//        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.priority_array, R.layout.fragment_edit_item)

    }
}