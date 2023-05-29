package com.example.android1.domain.weather

data class WeatherDetailedInfo(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windDegree: Int,
    val windSpeed: Double,
    val sunriseTime: Int,
    val sunsetTime: Int,
    val weatherDescription: String,
    val weatherIcon: String
)
