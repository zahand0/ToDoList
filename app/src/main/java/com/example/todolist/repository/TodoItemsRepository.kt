package com.example.todolist.repository

import android.util.Log
import com.example.todolist.data.DataSource
import com.example.todolist.data.TodoItem
import com.example.todolist.data.database.ItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext


class TodoItemsRepository(private val database: ItemDatabase) {

    private val dao = database.itemDao()

    val todoItems: Flow<List<TodoItem>> = dao.getItems()
    val todoItemsUndone: Flow<List<TodoItem>> = dao.getUndoneItems()

    suspend fun addItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    suspend fun retrieveItem(id: Int): Flow<TodoItem>? {
        return withContext(Dispatchers.IO) {
            val res = dao.getItem(id)
            Log.d("repository", "comleted res")
            res
        }
    }

    suspend fun updateItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            dao.update(item)
        }
    }

    suspend fun deleteItem(itemId: Int) {
        withContext(Dispatchers.IO) {
            dao.delete(itemId)
        }
    }

    suspend fun getNumberDoneItems():Flow<Int> {
        return withContext(Dispatchers.IO) {
            dao.getNumberDoneItems()
        }
    }

}