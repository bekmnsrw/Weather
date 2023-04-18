package com.example.android1.data.weather.datasource.remote

import com.example.android1.data.weather.datasource.remote.response.MultipleWeatherResponse
import com.example.android1.data.weather.datasource.remote.response.Sys
import com.example.android1.data.weather.datasource.remote.response.WeatherResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WeatherApi {

    @GET("weather")
    fun getCityId(
        @Query("q") cityName: String
    ): Single<Sys>

    @GET("weather")
    fun getWeather(
        @Query("id") cityId: Int
    ): Single<WeatherResponse>

    @GET("find")
    fun getWeatherInNearbyCities(
        @QueryMap map: Map<String, String>
    ): Single<MultipleWeatherResponse>
}
