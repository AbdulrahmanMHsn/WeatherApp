package com.example.myapplication.network


import amhsn.weatherapp.network.response.ResponseAPIWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("data/2.5/onecall")
    fun getWeather(@Query("lat") lat: Double
                  , @Query("lon") lon: Double
//                  , @Query("exclude") exclude: String
                  , @Query("appid") appid: String): Call<ResponseAPIWeather>

}