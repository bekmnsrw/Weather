package com.example.android1.data.weather.mapper

import com.example.android1.R
import com.example.android1.data.weather.datasource.remote.response.MultipleCity
import com.example.android1.data.weather.datasource.remote.response.WeatherResponse
import com.example.android1.domain.weather.WeatherDetailedInfo
import com.example.android1.domain.weather.WeatherMainInfo

fun WeatherResponse.toWeatherDetailedInfo(): WeatherDetailedInfo = WeatherDetailedInfo(
    cityName = name,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    pressure = main.pressure,
    windDegree = wind.deg,
    windSpeed = wind.speed,
    sunriseTime = sys.sunrise,
    sunsetTime = sys.sunset,
    weatherDescription = weather.first().description,
    weatherIcon = weather.first().icon
)

fun MultipleCity.toWeatherMainInfo(): WeatherMainInfo = WeatherMainInfo(
    cityId = id,
    weatherIcon = weather.first().icon,
    cityName = name,
    temperature = main.temp,
    calculateColor(main.temp)
)

fun List<MultipleCity>.toWeatherMainInfoList(): List<WeatherMainInfo> = map {
    it.toWeatherMainInfo()
}

private fun calculateColor(
    temperature: Double
): Int = when {
    temperature < -30.0 -> {
        R.color.violet
    }
    temperature in -30.0..-15.0 -> {
        R.color.dark_blue
    }
    temperature in -15.0..0.0 -> {
        R.color.blue
    }
    temperature in 0.0..15.0 -> {
        R.color.green
    }
    temperature in 15.0..30.0 -> {
        R.color.yellow
    }
    temperature > 30.0 -> {
        R.color.orange
    }
    else -> {
        R.color.black
    }
}
