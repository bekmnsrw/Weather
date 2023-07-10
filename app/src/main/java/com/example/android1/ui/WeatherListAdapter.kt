package com.example.android1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.android1.data.WeatherUIModel
import com.example.android1.databinding.ItemWeatherBinding

class WeatherListAdapter(
    private val onItemClick: (Int) -> Unit
): ListAdapter<WeatherUIModel, WeatherViewHolder>(object : DiffUtil.ItemCallback<WeatherUIModel>() {

    override fun areItemsTheSame(
        oldItem: WeatherUIModel,
        newItem: WeatherUIModel
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: WeatherUIModel,
        newItem: WeatherUIModel
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
