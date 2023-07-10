package com.example.android1.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class UnitsOfMeasurementInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalUrl = chain.request()
        val newUrl = originalUrl.url.newBuilder()
            .addQueryParameter("units", UNITS_OF_MEASUREMENT)
            .build()

        return chain.proceed(
            originalUrl.newBuilder()
                .url(newUrl)
                .build()
        )
    }

    companion object {
        private const val UNITS_OF_MEASUREMENT = "metric"
    }
}
