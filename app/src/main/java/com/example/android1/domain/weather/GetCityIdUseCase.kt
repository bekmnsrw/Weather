package com.example.android1.domain.weather

class GetCityIdUseCase(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        cityName: String
    ): Int = weatherRepository.getCityId(cityName)
}
