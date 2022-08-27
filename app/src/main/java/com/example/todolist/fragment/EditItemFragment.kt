package com.example.todolist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.todolist.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.datePickerSwitch?.setOnCheckedChangeListener { compoundButton, b ->
            when (b) {
                true -> {
                    val datePicker =
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select date")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build()

                    datePicker.show(childFragmentManager, "datePicker")

                    datePicker.addOnPositiveButtonClickListener {

                        val date = Date(it)
                        val dateString =
                            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(date)
                                .lowercase()
                        binding?.pickedDate?.text = dateString
                        binding?.pickedDate?.visibility = View.VISIBLE
                    }
                    datePicker.addOnNegativeButtonClickListener {
                        compoundButton.isChecked = false
                    }
                    datePicker.addOnCancelListener {
                        compoundButton.isChecked = false
                    }

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