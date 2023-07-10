package com.example.android1.data

import androidx.annotation.ColorRes

data class WeatherUIModel(
    val id: Int,
    val icon: String,
    val cityName: String,
    val temperature: Double,
    @ColorRes val temperatureTextColor: Int
)
