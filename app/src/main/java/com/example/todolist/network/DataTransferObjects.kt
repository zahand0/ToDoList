package com.example.todolist.network


import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkItemContainer(val todoItems: List<NetworkItem>)

@JsonClass(generateAdapter = true)
data class NetworkItem(
    val id: Int,
    val description: String,
    val priority: TaskPriority,
    val isDone: Boolean,
    val deadlineDate: Long?,
    val creationDate: Long,
    val lastEditDate: Long
)

fun NetworkItemContainer.asDatabaseModel(): List<TodoItem> {
    return todoItems.map {
        TodoItem(
            id = it.id,
            description = it.description,
            priority = it.priority,
            isDone = it.isDone,
            deadlineDate = it.deadlineDate,
            creationDate = it.creationDate,
            lastEditDate = it.lastEditDate
        )
    }
}