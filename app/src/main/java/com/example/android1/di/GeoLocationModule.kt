package com.example.android1.di

import android.content.Context
import com.example.android1.data.geolocation.GeoLocationRepositoryImpl
import com.example.android1.domain.geolocation.GeoLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module(includes = [BindGeoLocationModule::class])
@InstallIn(ViewModelComponent::class)
class GeoLocationModule {

    @Provides
    fun provideLocationClient(
        applicationContext: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
}

@Module
@InstallIn(ViewModelComponent::class)
interface BindGeoLocationModule {

    @Binds
    fun bindGeoLocationRepository(
        geoLocationRepositoryImpl: GeoLocationRepositoryImpl
    ): GeoLocationRepository
}
