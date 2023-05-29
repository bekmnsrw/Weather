package com.example.android1.domain.weather

class GetWeatherDetailedInfoUseCase(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        cityId: Int
    ): WeatherDetailedInfo = weatherRepository.getWeather(cityId)
}
