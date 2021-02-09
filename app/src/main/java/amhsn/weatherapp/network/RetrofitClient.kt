package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun getInstance():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    fun getApiService():ApiInterface{
        return getInstance().create(ApiInterface::class.java)
    }
}