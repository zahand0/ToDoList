package com.example.todolist.repository

import com.example.todolist.data.TaskModel
import com.example.todolist.network.exception.NetworkState
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTaskItems(): Flow<List<TaskModel>>

    suspend fun addItem(item: TaskModel)

    suspend fun retrieveItem(id: Int): Flow<TaskModel>?

    suspend fun updateItem(item: TaskModel)

    suspend fun deleteItem(itemId: Int)

    suspend fun getNumberDoneItems(): Flow<Int>

    suspend fun refreshItems(): NetworkState
}