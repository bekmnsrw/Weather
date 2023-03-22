package com.example.android1.domain.geolocation

class GetGeoLocationUseCase(
    private val geoLocationRepository: GeoLocationRepository
) {
    suspend operator fun invoke(
        arePermissionsGranted: Boolean
    ): GeoLocation = geoLocationRepository.getLocation(arePermissionsGranted)
}
