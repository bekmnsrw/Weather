package com.example.android1.domain.weather

import javax.inject.Inject

class GetWeatherMainInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        query: Map<String, String>,
        isLocal: Boolean
    ): List<WeatherMainInfo> = weatherRepository.getWeatherInNearbyCities(query, isLocal)
}
