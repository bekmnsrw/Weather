package com.example.android1.domain.weather

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetWeatherMainInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    operator fun invoke(
        query: Map<String, String>,
        isLocal: Boolean
    ): Single<List<WeatherMainInfo>> = weatherRepository.getWeatherInNearbyCities(query, isLocal)
}
