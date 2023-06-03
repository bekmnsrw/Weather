package com.example.android1.domain.geolocation

import io.reactivex.rxjava3.core.Single

interface GeoLocationRepository {

    fun getLocation(arePermissionsGranted: Boolean): Single<GeoLocation>
}
