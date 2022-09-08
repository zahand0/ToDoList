package com.example.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.data.database.ItemDatabase
import com.example.todolist.repository.TodoItemsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TodoItemsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TodoItemsRepository(ItemDatabase.getDatabase(application))


    val allItems: StateFlow<List<TodoItem>> =
        repository.todoItems.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

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
            0,
            description,
            priority,
            isDone,
            deadlineDate,
            creationDate,
            editDate
        )
        viewModelScope.launch {
            repository.addItem(item)
        }


    }

    fun retrieveItem(id: Int): TodoItem? {
        return runBlocking(Dispatchers.IO) {
            Log.d("repoViewModel", "call retrieveItem")
            val res = repository.retrieveItem(id)
            Log.d("repoViewModel", "result")
            res?.first()
        }
    }

    fun updateItem(
        id: Int,
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
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    private fun getDoneTasksCount(tasksList: List<TodoItem>): Int {
        return tasksList.filter { it.isDone }.size
    }
}