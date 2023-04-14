package com.example.android1.di

import com.example.android1.data.weather.WeatherRepositoryImpl
import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.domain.weather.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module(includes = [BindWeatherModule::class])
@InstallIn(ViewModelComponent::class)
class WeatherModule {

    @Provides
    fun provideWeatherMainInfoCache(): WeatherMainInfoCache = WeatherMainInfoCache
}

@Module
@InstallIn(ViewModelComponent::class)
interface BindWeatherModule {

    @Binds
    fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}
