package com.example.todoapp.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api-drab-six.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}