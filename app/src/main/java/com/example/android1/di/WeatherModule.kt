package com.example.android1.di

import com.example.android1.data.weather.WeatherRepositoryImpl
import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.domain.weather.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [BindWeatherModule::class])
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Provides
    fun provideWeatherMainInfoCache(): WeatherMainInfoCache = WeatherMainInfoCache
}

@Module
@InstallIn(SingletonComponent::class)
interface BindWeatherModule {

    @Binds
    fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}
