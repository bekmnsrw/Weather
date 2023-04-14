package com.example.android1.data.geolocation

import android.annotation.SuppressLint
import com.example.android1.domain.geolocation.GeoLocation
import com.example.android1.domain.geolocation.GeoLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GeoLocationRepositoryImpl @Inject constructor(
    private val client: FusedLocationProviderClient
) : GeoLocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(arePermissionsGranted: Boolean): GeoLocation {
        return if (arePermissionsGranted) {
            client.lastLocation.await().let {
                GeoLocation(
                    longitude = it.longitude,
                    latitude = it.latitude
                )
            }
        } else {
            GeoLocation(
                longitude = null,
                latitude = null
            )
        }
    }
}
