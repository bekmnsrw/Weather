package com.example.android1.di.module

import com.example.android1.data.weather.WeatherRepositoryImpl
import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.data.weather.datasource.remote.WeatherApi
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherDetailedInfoUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.example.android1.domain.weather.WeatherRepository
import dagger.Module
import dagger.Provides

@Module
class WeatherModule {

    @Provides
    fun provideWeatherRepository(
        weatherApi: WeatherApi,
        weatherMainInfoCache: WeatherMainInfoCache
    ): WeatherRepository = WeatherRepositoryImpl(weatherApi, weatherMainInfoCache)

    @Provides
    fun provideWeatherMainInfoCache(): WeatherMainInfoCache = WeatherMainInfoCache

    @Provides
    fun provideWeatherDetailedInfoUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherDetailedInfoUseCase = GetWeatherDetailedInfoUseCase(weatherRepository)

    @Provides
    fun provideWeatherMainInfoUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherMainInfoUseCase = GetWeatherMainInfoUseCase(weatherRepository)

    @Provides
    fun provideCityIdUseCase(
        weatherRepository: WeatherRepository
    ): GetCityIdUseCase = GetCityIdUseCase(weatherRepository)
}
