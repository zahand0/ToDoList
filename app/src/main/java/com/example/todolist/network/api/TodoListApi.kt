package com.example.todolist.network.api

import com.example.todolist.network.exception.ResultCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "http://10.0.2.2:5000/"

object TodoListApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(ResultCallAdapterFactory())
        .build()

    val api = retrofit.create(TodoListApiService::class.java)
}