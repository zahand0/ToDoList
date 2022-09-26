package com.example.todolist.network.api

import com.example.todolist.network.NetworkItemContainer
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TodoListApiService {
    @Headers("token: 12345678")
    @POST("get_all_tasks")
    suspend fun updateTaskList(@Body container: NetworkItemContainer): Result<NetworkItemContainer>
}