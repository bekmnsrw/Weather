package com.example.android1.data.weather

import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.data.weather.datasource.remote.WeatherApi
import com.example.android1.data.weather.mapper.toWeatherDetailedInfo
import com.example.android1.data.weather.mapper.toWeatherMainInfoList
import com.example.android1.domain.weather.WeatherDetailedInfo
import com.example.android1.domain.weather.WeatherMainInfo
import com.example.android1.domain.weather.WeatherRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherMainInfoCache: WeatherMainInfoCache
) : WeatherRepository {

    override fun getWeather(
        cityId: Int
    ): Single<WeatherDetailedInfo> = weatherApi.getWeather(cityId)
        .observeOn(Schedulers.computation())
        .map { it.toWeatherDetailedInfo() }
        .subscribeOn(Schedulers.io())

    override fun getWeatherInNearbyCities(
        query: Map<String, String>,
        isLocal: Boolean
    ): Single<List<WeatherMainInfo>> =
        if (isLocal) {
            Observable.fromIterable(weatherMainInfoCache.cache).toList()
        } else {
            weatherApi.getWeatherInNearbyCities(query)
                .observeOn(Schedulers.computation())
                .map { response ->
                    response.list.toWeatherMainInfoList().also {
                        weatherMainInfoCache.cache = it.toMutableList()
                    }
                }
                .subscribeOn(Schedulers.io())
        }


    override fun getCityId(
        cityName: String
    ): Single<Int> = weatherApi.getCityId(cityName)
        .observeOn(Schedulers.computation())
        .map { it.id }
        .subscribeOn(Schedulers.io())
}
