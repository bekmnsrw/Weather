package com.example.android1.data.geolocation

import android.annotation.SuppressLint
import com.example.android1.domain.geolocation.GeoLocation
import com.example.android1.domain.geolocation.GeoLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GeoLocationRepositoryImpl @Inject constructor(
    private val client: FusedLocationProviderClient
) : GeoLocationRepository {

    @SuppressLint("MissingPermission")
    override fun getLocation(
        arePermissionsGranted: Boolean
    ): Single<GeoLocation> = Single.create { emitter ->
        if (arePermissionsGranted) {
            client.lastLocation
                .addOnSuccessListener {
                    emitter.onSuccess(
                        GeoLocation(
                            longitude = it.longitude,
                            latitude = it.latitude
                        )
                    )
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        } else {
            GeoLocation(
                longitude = null,
                latitude = null
            )
        }
    }
}
