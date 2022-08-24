package com.example.todolist.repository

import com.example.todolist.data.DataSource
import com.example.todolist.data.TodoItem


class TodoItemsRepository {

    val todoItems: List<TodoItem> = DataSource.todoItems.toList()
}
