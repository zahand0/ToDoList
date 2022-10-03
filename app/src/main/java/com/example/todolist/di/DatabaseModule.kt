package com.example.todolist.di

import android.content.Context
import com.example.todolist.data.database.ItemDatabase
import dagger.Component
import dagger.Module
import dagger.Provides


@Module
class DatabaseModule {

    companion object {
        @AppScope
        @Provides
        fun provideDatabase(context: Context): ItemDatabase {
            return ItemDatabase.getDatabase(context)
        }
    }
}