package com.example.todolist.ui.stateholders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.TaskModel
import com.example.todolist.network.exception.NetworkState
import com.example.todolist.repository.TaskRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

class TaskViewModel(private val repository: TaskRepository) :
    ViewModel() {

    val allItems: Flow<List<TaskModel>> =
        repository.getTaskItems()

    private val _doneTasks = getDoneTasksCount().stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val doneTasks = _doneTasks

    private var _lastRefreshStatus: NetworkState = NetworkState.OK
    val lastRefreshStatus: NetworkState
        get() = _lastRefreshStatus

    fun addItem(
        description: String,
        priority: TaskPriority,
        isDone: Boolean,
        deadlineDate: Long?,
        creationDate: Long,
        editDate: Long
    ) {

        val item = TaskModel(
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

    fun retrieveItem(id: Int): TaskModel? {
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
        val item = TaskModel(
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

    fun updateItem(item: TaskModel) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun changeStatus(item: TaskModel, status: Boolean) {
        val newItem = item.copy(isDone = status)
        updateItem(newItem)
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    suspend fun refreshItems() {
        withContext(viewModelScope.coroutineContext) {
            var result = repository.refreshItems()
            repeat(3) {
                if (result != NetworkState.OK) {
                    delay(500)
                    result = repository.refreshItems()
                } else return@repeat
            }
            _lastRefreshStatus = result
        }
    }

    private fun getDoneTasksCount(): Flow<Int> {
        return runBlocking(Dispatchers.IO) {
            repository.getNumberDoneItems().stateIn(viewModelScope, SharingStarted.Lazily, 0)
        }
    }
}