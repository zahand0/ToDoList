package com.example.todolist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.network.NetworkItem

@Entity(tableName = "item")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "priority")
    val priority: TaskPriority,
    @ColumnInfo(name = "is_done")
    val isDone: Boolean,
    @ColumnInfo(name = "deadline_date")
    val deadlineDate: Long?,
    @ColumnInfo(name = "creation_date")
    val creationDate: Long,
    @ColumnInfo(name = "last_edit_date")
    val lastEditDate: Long
)

fun TodoItem.asNetworkItem(): NetworkItem {
    return NetworkItem(
        id = this.id,
        description = this.description,
        priority = this.priority,
        isDone = this.isDone,
        deadlineDate = this.deadlineDate,
        creationDate = this.creationDate,
        lastEditDate = this.lastEditDate
    )
}
