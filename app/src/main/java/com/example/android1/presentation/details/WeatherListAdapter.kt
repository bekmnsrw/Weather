package com.example.android1.presentation.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.android1.databinding.ItemWeatherBinding
import com.example.android1.domain.weather.WeatherMainInfo

class WeatherListAdapter(
    private val onItemClick: (Int) -> Unit
): ListAdapter<WeatherMainInfo, WeatherViewHolder>(object : DiffUtil.ItemCallback<WeatherMainInfo>() {

    override fun areItemsTheSame(
        oldItem: WeatherMainInfo,
        newItem: WeatherMainInfo
    ): Boolean = oldItem.cityId == newItem.cityId

    override fun areContentsTheSame(
        oldItem: WeatherMainInfo,
        newItem: WeatherMainInfo
    ): Boolean = oldItem == newItem

}) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherViewHolder = WeatherViewHolder(ItemWeatherBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ),
        onItemClick
    )

    override fun onBindViewHolder(
        holder: WeatherViewHolder,
        position: Int
    ) {
        holder.onBind(currentList[position])
    }
}
