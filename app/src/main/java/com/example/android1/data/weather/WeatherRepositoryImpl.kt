package com.example.android1.data.weather

import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.data.weather.datasource.remote.WeatherApi
import com.example.android1.data.weather.mapper.toWeatherDetailedInfo
import com.example.android1.data.weather.mapper.toWeatherMainInfoList
import com.example.android1.domain.weather.WeatherDetailedInfo
import com.example.android1.domain.weather.WeatherMainInfo
import com.example.android1.domain.weather.WeatherRepository

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val weatherMainInfoCache: WeatherMainInfoCache
) : WeatherRepository {

    override suspend fun getWeather(
        cityId: Int
    ): WeatherDetailedInfo = weatherApi.getWeather(cityId).toWeatherDetailedInfo()

    override suspend fun getWeatherInNearbyCities(
        query: Map<String, String>,
        isLocal: Boolean
    ): List<WeatherMainInfo> =
        if (isLocal) {
            weatherMainInfoCache.cache
        } else {
            weatherApi.getWeatherInNearbyCities(query).list.toWeatherMainInfoList().also {
                weatherMainInfoCache.cache = it.toMutableList()
            }
        }

    override suspend fun getCityId(
        cityName: String
    ): Int = weatherApi.getCityId(cityName).id
}
