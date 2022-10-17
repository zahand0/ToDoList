package com.example.todolist.repository

import com.example.todolist.data.TaskModel
import com.example.todolist.network.exception.NetworkState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class FakeTaskRepository : TaskRepository {

    private val taskItems = mutableListOf<TaskModel>()

    private var taskItemsFlow = Channel<List<TaskModel>>()

    private var doneTaskFlow = Channel<Int>()

    private var networkState = NetworkState.OK

    fun setNetworkState(value: NetworkState) {
        networkState = value
    }

    private suspend fun updateFlow() {
        taskItemsFlow.send(taskItems)
    }

    override fun getTaskItems(): Flow<List<TaskModel>> {
        return taskItemsFlow.consumeAsFlow()
    }

    override suspend fun addItem(item: TaskModel) {
        taskItems.add(item)
        updateFlow()
    }

    override suspend fun retrieveItem(id: Int): Flow<TaskModel>? {
        return if (taskItems.find { it.id == id } != null) {
            flow { taskItems.find { it.id == id } }
        } else {
            null
        }
    }

    override suspend fun updateItem(item: TaskModel) {
        taskItems[taskItems.indexOfFirst { it.id == item.id }] = item
        updateFlow()
    }

    override suspend fun deleteItem(itemId: Int) {
        taskItems.removeIf { it.id == itemId }
        updateFlow()
    }

    override suspend fun getNumberDoneItems(): Flow<Int> {
        return doneTaskFlow.consumeAsFlow()
    }

    override suspend fun refreshItems(): NetworkState {
        return networkState
    }
}