package com.example.todolist

import android.app.Application
import com.example.todolist.data.database.ItemDatabase
import com.example.todolist.di.AppComponent
import com.example.todolist.di.DaggerRepositoryComponent
import com.example.todolist.di.RepositoryComponent

class TodoListApplication: Application() {

    private lateinit var repositoryComponent: RepositoryComponent

    override fun onCreate() {
        super.onCreate()
        repositoryComponent = DaggerRepositoryComponent.factory().create(this.applicationContext)
    }

    fun getRepositoryComponent(): RepositoryComponent = repositoryComponent

}