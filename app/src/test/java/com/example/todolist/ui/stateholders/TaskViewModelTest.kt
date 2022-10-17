package com.example.todolist.ui.stateholders

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todolist.MainCoroutineRule
import com.example.todolist.data.TaskModel
import com.example.todolist.data.TaskPriority
import com.example.todolist.network.exception.NetworkState
import com.example.todolist.repository.FakeTaskRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.internal.artificialFrame
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TaskViewModel
    private lateinit var repository: FakeTaskRepository

    @Before
    fun setup() {
        repository = FakeTaskRepository()
        viewModel = TaskViewModel(repository)
    }

    @Test
    fun `add item works`() = runTest {
        viewModel.addItem(
            "",
            TaskPriority.NORMAL,
            false,
            null,
            1,
            1
        )
        assertThat(viewModel.allItems.first()).isNotEmpty()
    }


    @Test
    fun `allItems is correct`() = runTest {
        viewModel.addItem(
            "",
            TaskPriority.NORMAL,
            false,
            null,
            1,
            1
        )
        viewModel.addItem(
            "",
            TaskPriority.NORMAL,
            false,
            null,
            1,
            1
        )
        assertThat(viewModel.allItems.first()).hasSize(2)
    }

    @Test
    fun `refreshItems returns OK when NetworkState is OK`() = runTest {
        repository.setNetworkState(NetworkState.OK)
        viewModel.refreshItems()
        assertThat(viewModel.lastRefreshStatus).isEqualTo(NetworkState.OK)
    }

    @Test
    fun `refreshItems returns UNDEFINED_ERROR when NetworkState is UNDEFINED_ERROR`() = runTest {
        repository.setNetworkState(NetworkState.UNDEFINED_ERROR)
        viewModel.refreshItems()
        assertThat(viewModel.lastRefreshStatus).isEqualTo(NetworkState.UNDEFINED_ERROR)
    }

    @Test
    fun `refreshItems returns CONNECTION_ERROR when NetworkState is CONNECTION_ERROR`() = runTest {
        repository.setNetworkState(NetworkState.CONNECTION_ERROR)
        viewModel.refreshItems()
        assertThat(viewModel.lastRefreshStatus).isEqualTo(NetworkState.CONNECTION_ERROR)
    }

}