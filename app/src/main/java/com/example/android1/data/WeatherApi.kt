package com.example.android1.data

import com.example.android1.data.response.MultipleWeatherResponse
import com.example.android1.data.response.Sys
import com.example.android1.data.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WeatherApi {

    @GET("weather")
    suspend fun getCityId(
        @Query("q") cityName: String
    ): Sys

    @GET("weather")
    suspend fun getWeather(
        @Query("id") cityId: Int
    ): WeatherResponse

    @GET("find")
    suspend fun getWeatherInNearbyCities(
        @QueryMap map: Map<String, String>
    ): MultipleWeatherResponse
}
