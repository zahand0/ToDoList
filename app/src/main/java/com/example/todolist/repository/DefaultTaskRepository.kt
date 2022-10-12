package com.example.todolist.repository

import android.util.Log
import com.example.todolist.data.TaskModel
import com.example.todolist.data.asNetworkItem
import com.example.todolist.data.dao.TaskDao
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

class DefaultTaskRepository @Inject constructor(
    private val dao: TaskDao,
    private val retrofitClient: RetrofitClient
) : TaskRepository {

    companion object {
        private const val TAG = "Repository"
    }

    override fun getTaskItems(): Flow<List<TaskModel>> {
        return dao.getItems()
    }

    override suspend fun addItem(item: TaskModel) {
        withContext(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    override suspend fun retrieveItem(id: Int): Flow<TaskModel>? {
        return withContext(Dispatchers.IO) {
            val res = dao.getItem(id)
            Log.d("repository", "comleted res")
            res
        }
    }

    override suspend fun updateItem(item: TaskModel) {
        withContext(Dispatchers.IO) {
            dao.update(item)
        }
    }

    override suspend fun deleteItem(itemId: Int) {
        withContext(Dispatchers.IO) {
            dao.delete(itemId)
        }
    }

    override suspend fun getNumberDoneItems(): Flow<Int> {
        return withContext(Dispatchers.IO) {
            dao.getNumberDoneItems()
        }
    }

    override suspend fun refreshItems(): NetworkState {
        var result = NetworkState.UNDEFINED_ERROR
        var error = "ok"
        withContext(Dispatchers.IO) {

            val taskList = getTaskItems().first().map { it.asNetworkItem() }
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