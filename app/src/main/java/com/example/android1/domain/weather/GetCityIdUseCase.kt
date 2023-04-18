package com.example.android1.domain.weather

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetCityIdUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    operator fun invoke(
        cityName: String
    ): Single<Int> = weatherRepository.getCityId(cityName)
}
