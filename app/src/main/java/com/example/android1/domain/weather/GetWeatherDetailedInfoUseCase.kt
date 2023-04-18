package com.example.android1.domain.weather

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetWeatherDetailedInfoUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    operator fun invoke(
        cityId: Int
    ): Single<WeatherDetailedInfo> = weatherRepository.getWeather(cityId)
}
