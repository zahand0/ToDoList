package com.example.todolist.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.todolist.data.TaskModel
import com.example.todolist.data.TaskPriority
import com.example.todolist.data.database.TaskDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TaskDaoTest {

//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.taskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insert() = runTest {
            val taskItem = TaskModel(
                1,
                "description",
                TaskPriority.NORMAL,
                false,
                1000,
                1,
                500
            )
            dao.insert(taskItem)

            val allTaskItems = dao.getItems().first()

            assertThat(allTaskItems).contains(taskItem)
        }

    @Test
    fun delete() = runTest {
        val taskItem = TaskModel(
            1,
            "description",
            TaskPriority.NORMAL,
            false,
            1000,
            1,
            500
        )
        dao.insert(taskItem)
        dao.delete(taskItem.id)

        val allTaskItems = dao.getItems().first()

        assertThat(allTaskItems).doesNotContain(taskItem)
    }

    @Test
    fun update() = runTest {
        val taskItem = TaskModel(
            1,
            "description",
            TaskPriority.NORMAL,
            false,
            1000,
            1,
            500
        )
        dao.insert(taskItem)

        val taskItemUpdated = taskItem.copy(isDone = true)

        dao.update(taskItemUpdated)

        val allTaskItems = dao.getItems().first()

        assertThat(allTaskItems).doesNotContain(taskItem)
        assertThat(allTaskItems).contains(taskItemUpdated)
    }

    @Test
    fun getItem() = runTest {
        val taskItem = TaskModel(
            1,
            "description",
            TaskPriority.NORMAL,
            false,
            1000,
            1,
            500
        )
        dao.insert(taskItem)

        val oneTaskItem = dao.getItem(taskItem.id)?.first()

        assertThat(oneTaskItem).isEqualTo(taskItem)
    }

    @Test
    fun getItems() = runTest {
        val taskItem1 = TaskModel(
            1,
            "description",
            TaskPriority.NORMAL,
            false,
            1000,
            1,
            500
        )
        val taskItem2 = TaskModel(
            2,
            "description2",
            TaskPriority.NORMAL,
            true,
            1000,
            2,
            500
        )
        val taskItem3 = TaskModel(
            3,
            "description3",
            TaskPriority.LOW,
            false,
            500,
            3,
            1000
        )
        dao.insert(taskItem1)
        dao.insert(taskItem2)
        dao.insert(taskItem3)

        val allTaskItems = dao.getItems().first()

        assertThat(allTaskItems).containsExactlyElementsIn(listOf(taskItem1, taskItem2, taskItem3))
    }

    @Test
    fun getNumberDoneItems() = runTest {
        val taskItem1 = TaskModel(
            1,
            "description",
            TaskPriority.NORMAL,
            false,
            1000,
            1,
            500
        )
        val taskItem2 = TaskModel(
            2,
            "description2",
            TaskPriority.NORMAL,
            true,
            1000,
            2,
            500
        )
        val taskItem3 = TaskModel(
            3,
            "description3",
            TaskPriority.LOW,
            true,
            500,
            3,
            1000
        )
        dao.insert(taskItem1)
        dao.insert(taskItem2)
        dao.insert(taskItem3)

        val allTaskItems = dao.getNumberDoneItems().first()

        assertThat(allTaskItems).isEqualTo(2)
    }
    }