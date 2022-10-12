package com.example.todolist.repository

import com.example.todolist.data.TaskModel
import com.example.todolist.network.exception.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeTaskRepository : TaskRepository {

    private val taskItems = mutableListOf<TaskModel>()

    private val taskItemsFlow = MutableStateFlow(taskItems)

    private var networkState = NetworkState.OK

    fun setNetworkState(value: NetworkState) {
        networkState = value
    }

    private fun updateFlow() {
        taskItemsFlow.value = taskItems
    }

    override fun getTaskItems(): Flow<List<TaskModel>> {
        return taskItemsFlow
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
        return flow { emit(taskItems.count { it.isDone }) }
    }

    override suspend fun refreshItems(): NetworkState {
        return networkState
    }
}