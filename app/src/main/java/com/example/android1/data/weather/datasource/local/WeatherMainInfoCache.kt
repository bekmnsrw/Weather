package com.example.android1.data.weather.datasource.local

import com.example.android1.domain.weather.WeatherMainInfo

object WeatherMainInfoCache {
    var cache: MutableList<WeatherMainInfo> = mutableListOf()
}
