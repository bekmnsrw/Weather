package com.example.android1.di.module

import android.content.Context
import com.example.android1.data.geolocation.GeoLocationRepositoryImpl
import com.example.android1.domain.geolocation.GeoLocationRepository
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class GeoLocationModule {

    @Provides
    fun provideLocationClient(
        applicationContext: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)

    @Provides
    fun provideGeoLocationRepository(
        fusedLocationProviderClient: FusedLocationProviderClient
    ): GeoLocationRepository = GeoLocationRepositoryImpl(fusedLocationProviderClient)

    @Provides
    fun provideGeoLocationUseCase(
        geoLocationRepository: GeoLocationRepository
    ): GetGeoLocationUseCase = GetGeoLocationUseCase(geoLocationRepository)
}
