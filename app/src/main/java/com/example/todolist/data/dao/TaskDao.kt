package com.example.todolist.data.dao

import androidx.room.*
import com.example.todolist.data.TaskModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TaskModel)

    @Update
    suspend fun update(item: TaskModel)

    @Query("DELETE FROM item WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM item WHERE id = :id")
    fun getItem(id: Int): Flow<TaskModel>?

    @Query("SELECT * FROM item ORDER BY creation_date ASC")
    fun getItems(): Flow<List<TaskModel>>

    @Query("SELECT * FROM item WHERE is_done = 0 ORDER BY creation_date ASC")
    fun getUndoneItems(): Flow<List<TaskModel>>

    @Query("SELECT COUNT(*) FROM item WHERE is_done = 1")
    fun getNumberDoneItems(): Flow<Int>
}