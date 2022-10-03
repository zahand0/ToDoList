package com.example.todolist.network.api

import com.example.todolist.network.exception.ResultCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TodoListApi {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:5000/"
//        private const val BASE_URL = "https://f0a1-178-176-181-18.eu.ngrok.io"

    }

    private var retrofit: Retrofit? = null
    private var api: TodoListApiService? = null

    private fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(ResultCallAdapterFactory())
                .build()
        }
        return retrofit!!
    }

    fun getApi(): TodoListApiService {
        if (api == null) {
            api = getClient().create(TodoListApiService::class.java)
        }
        return api!!
    }
}