package com.example.todolist.ui.stateholders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.repository.TodoItemsRepository

class TodoItemViewModelFactory(
    val repository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
