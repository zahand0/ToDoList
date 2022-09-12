package com.example.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TodoItem
import com.example.todolist.data.database.ItemDatabase
import com.example.todolist.repository.TodoItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoItemsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TodoItemsRepository(ItemDatabase.getDatabase(application))


    val allItems: Flow<List<TodoItem>> =
        repository.todoItems

    val undoneItems: Flow<List<TodoItem>> =
        repository.todoItemsUndone

    private val _doneTasks = getDoneTasksCount().stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val doneTasks = _doneTasks


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

    fun updateItem(item: TodoItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun changeStatus(item: TodoItem, status: Boolean) {
        val newItem = item.copy(isDone = status)
        updateItem(newItem)
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    private fun getDoneTasksCount(): Flow<Int> {
        return runBlocking(Dispatchers.IO) {
            repository.getNumberDoneItems().stateIn(viewModelScope, SharingStarted.Lazily, 0)
        }
    }
}