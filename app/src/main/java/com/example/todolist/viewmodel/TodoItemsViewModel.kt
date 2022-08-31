package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoItemsViewModel : ViewModel() {

    private val todoItemsRepository = TodoItemsRepository()


    val allItems: StateFlow<List<TodoItem>> = todoItemsRepository.todoItems
    val _doneTasks = MutableStateFlow(getDoneTasksCount(allItems.value))
    val doneTasks = _doneTasks.asStateFlow()

    fun addItem(
        description: String,
        priority: TaskPriority,
        isDone: Boolean,
        creationDate: Long,
        editDate: Long
    ) {

        val item = TodoItem(
            "",
            description,
            priority,
            isDone,
            null,
            creationDate,
            editDate
        )
        todoItemsRepository.addItem(item)

    }

    private fun getDoneTasksCount(tasksList: List<TodoItem>):Int {
        return tasksList.filter { it.isDone }.size
    }
}