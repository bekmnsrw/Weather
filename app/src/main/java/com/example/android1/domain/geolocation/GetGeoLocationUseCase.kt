package com.example.android1.domain.geolocation

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetGeoLocationUseCase @Inject constructor(
    private val geoLocationRepository: GeoLocationRepository
) {
    operator fun invoke(
        arePermissionsGranted: Boolean
    ): Single<GeoLocation> = geoLocationRepository.getLocation(arePermissionsGranted)
}
