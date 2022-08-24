package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todolist.data.TodoItem
import com.example.todolist.repository.TodoItemsRepository

class TodoItemsViewModel: ViewModel() {

    private val todoItemsRepository = TodoItemsRepository()

    val allItems: List<TodoItem> = todoItemsRepository.todoItems
}