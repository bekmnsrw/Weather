package com.example.android1.data.interceptor

import com.example.android1.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalUrl = chain.request()
        val newUrl = originalUrl.url.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .build()

        return chain.proceed(
            originalUrl.newBuilder()
                .url(newUrl)
                .build()
        )
    }

    companion object {
        private const val API_KEY = BuildConfig.API_KEY
    }
}
