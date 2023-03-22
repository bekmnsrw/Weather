package com.example.android1.domain.geolocation

interface GeoLocationRepository {

    suspend fun getLocation(arePermissionsGranted: Boolean): GeoLocation
}
