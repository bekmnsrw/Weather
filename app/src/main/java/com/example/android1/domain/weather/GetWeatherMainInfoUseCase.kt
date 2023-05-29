package com.example.android1.domain.weather

class GetWeatherMainInfoUseCase(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        query: Map<String, String>,
        isLocal: Boolean
    ): List<WeatherMainInfo> = weatherRepository.getWeatherInNearbyCities(query, isLocal)
}
