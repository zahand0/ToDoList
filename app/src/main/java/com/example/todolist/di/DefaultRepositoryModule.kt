package com.example.todolist.di

import com.example.todolist.repository.DefaultTaskRepository
import com.example.todolist.repository.TaskRepository
import dagger.Module
import dagger.Provides

@Module
class DefaultRepositoryModule {

    @Provides
    fun provideRepository(repository: DefaultTaskRepository): TaskRepository {
        return repository
    }
}