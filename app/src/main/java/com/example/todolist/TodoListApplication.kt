package com.example.todolist

import android.app.Application
import com.example.todolist.data.database.ItemDatabase

class TodoListApplication: Application() {

    val database: ItemDatabase by lazy { ItemDatabase.getDatabase(this) }
}