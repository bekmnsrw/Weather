package com.example.android1.data.response

import com.google.gson.annotations.SerializedName

data class MultipleWeatherResponse(
    @SerializedName("cod")
    val cod: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("list")
    val list: List<MultipleCity>,
    @SerializedName("message")
    val message: String
)

data class MultipleWind(
    @SerializedName("deg")
    val deg: Int,
    @SerializedName("speed")
    val speed: Double
)

data class MultipleWeather(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String
)

data class MultipleSys(
    @SerializedName("country")
    val country: String
)

data class MultipleMain(
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("temp_min")
    val tempMin: Double
)

data class MultipleCoord(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double
)

data class MultipleClouds(
    @SerializedName("all")
    val all: Int
)

data class MultipleCity(
    @SerializedName("clouds")
    val clouds: MultipleClouds,
    @SerializedName("coord")
    val coord: MultipleCoord,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: MultipleMain,
    @SerializedName("name")
    val name: String,
    @SerializedName("rain")
    val rain: Any,
    @SerializedName("snow")
    val snow: Any,
    @SerializedName("sys")
    val sys: MultipleSys,
    @SerializedName("weather")
    val weather: List<MultipleWeather>,
    @SerializedName("wind")
    val wind: MultipleWind
)
