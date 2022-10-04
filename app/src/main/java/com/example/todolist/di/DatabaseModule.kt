package com.example.todolist.di

import android.content.Context
import com.example.todolist.data.database.TaskDatabase
import dagger.Module
import dagger.Provides


@Module
class DatabaseModule {

    companion object {
        @AppScope
        @Provides
        fun provideDatabase(context: Context): TaskDatabase {
            return TaskDatabase.getDatabase(context)
        }
    }
}