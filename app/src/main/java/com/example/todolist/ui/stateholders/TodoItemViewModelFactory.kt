package com.example.todolist.ui.stateholders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TodoItemViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoItemsViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
