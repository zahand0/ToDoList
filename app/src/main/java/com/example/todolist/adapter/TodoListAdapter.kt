package com.example.todolist.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.databinding.TodoItemBinding

class TodoListAdapter(private val onItemClicked: (TodoItem) -> Unit) :
    ListAdapter<TodoItem, TodoListAdapter.ItemViewHolder>(DiffCallBack) {


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
        holder.bind(current)
    }

    class ItemViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoItem) {
            binding.taskDate.text = item.deadlineDate
            binding.taskDescription.text = item.description
            binding.taskStatus.isChecked = item.isDone
            binding.taskImportance.text = when (item.priority) {
                TaskPriority.URGENT -> {
                    binding.taskImportance.setTextColor(Color.RED)
                    binding.taskStatus.buttonTintList = ColorStateList.valueOf(Color.RED)
                    "!!"
                }
                TaskPriority.LOW -> {
                    binding.taskImportance.setTextColor(Color.GRAY)
                    "↓"
                }
                else -> {
                    binding.taskImportance.visibility = View.GONE
                    ""
                }
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