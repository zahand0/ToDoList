package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.repository.TodoItemsRepository
import kotlinx.coroutines.flow.*

class TodoItemsViewModel : ViewModel() {

    private val todoItemsRepository = TodoItemsRepository()


    val allItems: StateFlow<List<TodoItem>> = todoItemsRepository.todoItems
    val _doneTasks = MutableStateFlow(getDoneTasksCount(allItems.value))
    val doneTasks = _doneTasks.asStateFlow()

    fun addItem(
        description: String,
        priority: TaskPriority,
        isDone: Boolean,
        deadlineDate: Long?,
        creationDate: Long,
        editDate: Long
    ) {

        val item = TodoItem(
            "",
            description,
            priority,
            isDone,
            deadlineDate,
            creationDate,
            editDate
        )
        todoItemsRepository.addItem(item)

    }

    fun retrieveItem(id: String): StateFlow<TodoItem>? = todoItemsRepository.retrieveItem(id)

    fun updateItem(
        id: String,
        description: String,
        priority: TaskPriority,
        isDone: Boolean,
        deadlineDate: Long?,
        creationDate: Long,
        editDate: Long
    ) {
        val item = TodoItem(
            id,
            description,
            priority,
            isDone,
            deadlineDate,
            creationDate,
            editDate
        )
        todoItemsRepository.updateItem(item)
    }

    fun deleteItem(itemId: String) {
        todoItemsRepository.deleteItem(itemId)
    }

    private fun getDoneTasksCount(tasksList: List<TodoItem>):Int {
        return tasksList.filter { it.isDone }.size
    }
}