package com.example.android1.presentation

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android1.R
import com.example.android1.databinding.ItemWeatherBinding
import com.example.android1.domain.weather.WeatherMainInfo
import kotlin.math.roundToInt

class WeatherViewHolder(
    private val binding: ItemWeatherBinding,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var weatherModel: WeatherMainInfo? = null

    fun onBind(weatherModel: WeatherMainInfo) {
        this.weatherModel = weatherModel

        with(binding) {
            root.setOnClickListener {
                onItemClick(weatherModel.cityId)
            }

            tvCity.text = weatherModel.cityName

            tvTemperature.text =
                itemView.context.getString(R.string.temperature, weatherModel.temperature.roundToInt())

            tvTemperature
                .setTextColor(itemView.context.getColor(weatherModel.temperatureTextViewColor))

            ivIcon
                .load(itemView.context.getString(R.string.icon_load_path, weatherModel.weatherIcon))
        }
    }
}
