package com.example.android1.di

import com.example.android1.App.Companion.appContext
import com.example.android1.BuildConfig
import com.example.android1.data.core.interceptor.ApiKeyInterceptor
import com.example.android1.data.core.interceptor.UnitsOfMeasurementInterceptor
import com.example.android1.data.geolocation.GeoLocationRepositoryImpl
import com.example.android1.data.weather.WeatherRepositoryImpl
import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.data.weather.datasource.remote.WeatherApi
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherDetailedInfoUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DIContainer {

    private const val BASE_URL = BuildConfig.API_ENDPOINT
    private const val CONNECT_TIMEOUT = 10L

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(UnitsOfMeasurementInterceptor())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    private val weatherApi = retrofit.create(WeatherApi::class.java)
    private val weatherMainInfoCache = WeatherMainInfoCache
    private val weatherRepository = WeatherRepositoryImpl(weatherApi, weatherMainInfoCache)

    private var locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    private val geoLocationRepository = GeoLocationRepositoryImpl(locationClient)

    val weatherDetailedInfoUseCase: GetWeatherDetailedInfoUseCase
        get() = GetWeatherDetailedInfoUseCase(weatherRepository)

    val weatherMainInfoUseCase: GetWeatherMainInfoUseCase
        get() = GetWeatherMainInfoUseCase(weatherRepository)

    val cityIdUseCase: GetCityIdUseCase
        get() = GetCityIdUseCase(weatherRepository)

    val geoLocationUseCase: GetGeoLocationUseCase
        get() = GetGeoLocationUseCase(geoLocationRepository)
}
