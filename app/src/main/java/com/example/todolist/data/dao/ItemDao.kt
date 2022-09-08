package com.example.todolist.data.dao

import androidx.room.*
import com.example.todolist.data.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TodoItem)

    @Update
    suspend fun update(item: TodoItem)

    @Query("DELETE FROM item WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM item WHERE id = :id")
    fun getItem(id: Int): Flow<TodoItem>?

    @Query("SELECT * FROM item ORDER BY creation_date ASC")
    fun getItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM item WHERE is_done = 0 ORDER BY creation_date ASC")
    fun getUndoneItems(): Flow<List<TodoItem>>

    @Query("SELECT COUNT(*) FROM item WHERE is_done = 1")
    fun getNumberDoneItems(): Flow<Int>
}