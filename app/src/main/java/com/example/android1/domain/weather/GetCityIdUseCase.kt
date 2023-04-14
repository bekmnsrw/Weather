package com.example.android1.domain.weather

import javax.inject.Inject

class GetCityIdUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        cityName: String
    ): Int = weatherRepository.getCityId(cityName)
}
