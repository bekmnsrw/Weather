package com.example.android1.domain.weather

import io.reactivex.rxjava3.core.Single

interface WeatherRepository {

    fun getWeather(cityId: Int): Single<WeatherDetailedInfo>

    fun getWeatherInNearbyCities(
        query: Map<String, String>,
        isLocal: Boolean
    ): Single<List<WeatherMainInfo>>

    fun getCityId(cityName: String): Single<Int>
}
