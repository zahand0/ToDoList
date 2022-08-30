package com.example.todolist.repository

import com.example.todolist.data.CommonDateFormats
import com.example.todolist.data.DataSource
import com.example.todolist.data.TodoItem
import java.text.SimpleDateFormat
import java.util.*


class TodoItemsRepository {

    val todoItems: List<TodoItem> = DataSource.todoItems

    fun addItem(item: TodoItem) {
        DataSource.addItem(item)
    }
}