package com.example.todolist.ui.view

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.CommonDateFormats
import com.example.todolist.data.CommonDateFormats.msecToDate
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.databinding.TodoItemBinding
import java.lang.ref.WeakReference

class TodoListAdapter(private val onItemClicked: (TodoItem) -> Unit) :
    ListAdapter<TodoItem, TodoListAdapter.ItemViewHolder>(DiffCallBack) {

    var onCheckDoneClick: ((TodoItem, Boolean) -> Unit)? = null
    var onDeleteClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            TodoItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.onCheckDoneClick = onCheckDoneClick
        holder.onDeleteClick = onDeleteClick
        holder.bind(current)
    }

    class ItemViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val view = WeakReference(binding.root)

        var onDeleteClick: ((Int) -> Unit)? = null

        var onCheckDoneClick: ((TodoItem, Boolean) -> Unit)? = null

        init {
            view.get()?.let {
                if (it.scrollX != 0) {
                    it.scrollTo(0, 0)
                }
                binding.checkDone.setOnClickListener {
                    onCheckDoneClick?.let { onCheckDoneClick ->
//                        onCheckDoneClick(this)
                    }

                }

            }

        }

        fun bind(item: TodoItem) {

            binding.taskDescription.text = item.description
            setDeadlineDate(item.deadlineDate)
            setImportance(item.priority)
            Log.d("adapter", "bind call")

            binding.taskStatus.setOnCheckedChangeListener { compoundButton, b ->
                changeStatus(item, b)
            }
            binding.checkDone.setOnClickListener {
                changeStatus(item, true)
            }
            setCheckStatus(item.isDone)

            binding.deleteButton.setOnClickListener {
                onDeleteClick?.let { onDeleteClick ->
                    onDeleteClick(item.id)
                }
            }

        }

        private fun setImportance(priority: TaskPriority) {
            binding.taskImportance.text = when (priority) {
                TaskPriority.URGENT -> {
                    binding.taskImportance.setTextColor(Color.RED)
                    binding.taskImportance.visibility = View.VISIBLE
                    binding.taskStatus.buttonTintList =
                        this.itemView.context.getColorStateList(R.color.checkbox_urgency_color)
                    "!!"
                }
                TaskPriority.LOW -> {
                    binding.taskImportance.setTextColor(Color.GRAY)
                    binding.taskImportance.visibility = View.VISIBLE
                    binding.taskStatus.buttonTintList =
                        this.itemView.context.getColorStateList(R.color.checkbox_usual_color)
                    "↓"
                }
                else -> {
                    binding.taskImportance.visibility = View.GONE
                    binding.taskStatus.buttonTintList =
                        this.itemView.context.getColorStateList(R.color.checkbox_usual_color)
                    ""
                }
            }
        }

        private fun changeStatus(item: TodoItem, isDone: Boolean) {
            onCheckDoneClick?.let { it(item, isDone) }
        }

        private fun setCheckStatus(isDone: Boolean) {


            binding.taskStatus.isChecked = isDone

            if (isDone) {
                binding.taskDescription.isEnabled = false
                binding.taskDescription.paintFlags =
                    binding.taskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.taskDescription.isEnabled = true
                binding.taskDescription.paintFlags =
                    binding.taskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        private fun setDeadlineDate(deadlineDate: Long?) {
            if (deadlineDate == null) {
                binding.taskDate.visibility = View.GONE
                Log.d("adapter", "deadline is null!")
            } else {
                binding.taskDate.visibility = View.VISIBLE
                binding.taskDate.text = msecToDate(deadlineDate, CommonDateFormats.DIGIT_DATE)
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<TodoItem>() {
            override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem.description == newItem.description &&
                        oldItem.isDone == newItem.isDone &&
                        oldItem.deadlineDate == newItem.deadlineDate &&
                        oldItem.priority == newItem.priority
            }

        }
    }

}