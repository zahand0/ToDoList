package com.example.todolist.data

data class TodoItem(
    val id: String,
    val description: String,
    val priority: TaskPriority,
    val isDone: Boolean,
    val deadlineDate: String,
    val creationDate: String,
    val lastEditDate: String
)
