package com.example.android1.domain.geolocation

import javax.inject.Inject

class GetGeoLocationUseCase @Inject constructor(
    private val geoLocationRepository: GeoLocationRepository
) {
    suspend operator fun invoke(
        arePermissionsGranted: Boolean
    ): GeoLocation = geoLocationRepository.getLocation(arePermissionsGranted)
}
