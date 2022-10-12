package com.example.todolist.di

import android.content.Context
import com.example.todolist.repository.DefaultTaskRepository
import com.example.todolist.repository.TaskRepository
import dagger.Binds
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [DatabaseModule::class, DefaultRepositoryModule::class])
interface RepositoryComponent {

    fun getRepository(): TaskRepository

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): RepositoryComponent
    }
}