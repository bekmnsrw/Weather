package com.example.android1.domain.weather

interface WeatherRepository {

    suspend fun getWeather(cityId: Int): WeatherDetailedInfo

    suspend fun getWeatherInNearbyCities(
        query: Map<String, String>,
        isLocal: Boolean
    ): List<WeatherMainInfo>

    suspend fun getCityId(cityName: String): Int
}
