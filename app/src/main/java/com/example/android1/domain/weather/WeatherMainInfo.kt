package com.example.android1.domain.weather

data class WeatherMainInfo(
    val cityId: Int,
    val weatherIcon: String,
    val cityName: String,
    val temperature: Double,
    val temperatureTextViewColor: Int
)
