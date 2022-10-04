package com.example.todolist.ui.stateholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.repository.TaskRepository

class TodoItemViewModelFactory(
    val repository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
