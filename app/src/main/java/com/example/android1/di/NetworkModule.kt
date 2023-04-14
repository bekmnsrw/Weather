package com.example.android1.di

import com.example.android1.BuildConfig
import com.example.android1.data.core.interceptor.ApiKeyInterceptor
import com.example.android1.data.core.interceptor.UnitsOfMeasurementInterceptor
import com.example.android1.data.weather.datasource.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Named("logging_interceptor")
    fun provideLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Named("api_key_interceptor")
    fun provideApiKeyInterceptor(): Interceptor = ApiKeyInterceptor()

    @Provides
    @Named("units_of_measurement_interceptor")
    fun provideUnitsOfMeasurementInterceptor(): Interceptor = UnitsOfMeasurementInterceptor()

    @Provides
    fun provideHttpClient(
        @Named("logging_interceptor") loggingInterceptor: Interceptor,
        @Named("api_key_interceptor") apiKeyInterceptor: Interceptor,
        @Named("units_of_measurement_interceptor") unitsOfMeasurementInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(unitsOfMeasurementInterceptor)
        .connectTimeout(10L, TimeUnit.SECONDS)
        .build()

    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(gsonConverterFactory)
        .baseUrl(BuildConfig.API_ENDPOINT)
        .build()

    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun provideWeatherApi(
        retrofit: Retrofit
    ): WeatherApi = retrofit.create(WeatherApi::class.java)
}
