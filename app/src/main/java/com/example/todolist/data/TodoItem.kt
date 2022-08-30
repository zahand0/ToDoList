package com.example.todolist.data

data class TodoItem(
    val id: String,
    val description: String,
    val priority: TaskPriority,
    val isDone: Boolean,
    val deadlineDate: Long,
    val creationDate: Long,
    val lastEditDate: Long
)
