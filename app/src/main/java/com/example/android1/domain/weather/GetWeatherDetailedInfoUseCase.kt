package com.example.android1.domain.weather

import javax.inject.Inject

class GetWeatherDetailedInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        cityId: Int
    ): WeatherDetailedInfo = weatherRepository.getWeather(cityId)
}
