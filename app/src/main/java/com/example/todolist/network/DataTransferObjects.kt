package com.example.todolist.network


import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TaskModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkItemContainer(val taskItems: List<NetworkItem>)

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

fun NetworkItemContainer.asDatabaseModel(): List<TaskModel> {
    return taskItems.map {
        TaskModel(
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