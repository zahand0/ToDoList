package com.example.todolist.repository

import android.util.Log
import com.example.todolist.data.TaskModel
import com.example.todolist.data.asNetworkItem
import com.example.todolist.data.database.TaskDatabase
import com.example.todolist.network.NetworkItemContainer
import com.example.todolist.network.api.RetrofitClient
import com.example.todolist.network.asDatabaseModel
import com.example.todolist.network.exception.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val database: TaskDatabase,
    private val retrofitClient: RetrofitClient
) {

    companion object {
        private const val TAG = "Repository"
    }

    private val dao = database.taskDao()

    val taskItems: Flow<List<TaskModel>> = dao.getItems()
    val taskItemsUndone: Flow<List<TaskModel>> = dao.getUndoneItems()

    suspend fun addItem(item: TaskModel) {
        withContext(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    suspend fun retrieveItem(id: Int): Flow<TaskModel>? {
        return withContext(Dispatchers.IO) {
            val res = dao.getItem(id)
            Log.d("repository", "comleted res")
            res
        }
    }

    suspend fun updateItem(item: TaskModel) {
        withContext(Dispatchers.IO) {
            dao.update(item)
        }
    }

    suspend fun deleteItem(itemId: Int) {
        withContext(Dispatchers.IO) {
            dao.delete(itemId)
        }
    }

    suspend fun getNumberDoneItems(): Flow<Int> {
        return withContext(Dispatchers.IO) {
            dao.getNumberDoneItems()
        }
    }

    suspend fun refreshItems(): NetworkState {
        var result = NetworkState.UNDEFINED_ERROR
        var error = "ok"
        withContext(Dispatchers.IO) {

            val taskList = taskItems.first().map { it.asNetworkItem() }
            retrofitClient.getServices().updateTaskList(NetworkItemContainer(taskList))
                .onSuccess {
                    for (task in it.asDatabaseModel()) {
                        updateItem(task)
                    }
                    result = NetworkState.OK
                }
                .onFailure {
                    result = when (it) {
                        is HttpException -> NetworkState.UNDEFINED_ERROR
                        is IOException -> NetworkState.CONNECTION_ERROR
                        is RuntimeException -> NetworkState.CONNECTION_ERROR
                        else -> NetworkState.UNDEFINED_ERROR
                    }
                    error = it.localizedMessage ?: "error"
                }

        }
        Log.d(TAG, error)
        return result
    }
}