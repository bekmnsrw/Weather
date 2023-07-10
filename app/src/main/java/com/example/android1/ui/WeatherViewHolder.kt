package com.example.android1.ui

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android1.data.WeatherUIModel
import com.example.android1.databinding.ItemWeatherBinding
import kotlin.math.roundToInt

class WeatherViewHolder(
    private val binding: ItemWeatherBinding,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var weatherModel: WeatherUIModel? = null

    fun onBind(weatherUIModel: WeatherUIModel) {
        this.weatherModel = weatherUIModel

        with(binding) {
            root.setOnClickListener {
                onItemClick(weatherUIModel.id)
            }

            tvCity.text = weatherUIModel.cityName
            tvTemperature.text = "${weatherUIModel.temperature.roundToInt()}°"
            tvTemperature.setTextColor(itemView.context.getColor(weatherUIModel.temperatureTextColor))
            ivIcon.load("https://openweathermap.org/img/w/${weatherUIModel.icon}.png")
        }
    }
}
