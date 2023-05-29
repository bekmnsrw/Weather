package com.example.android1

import android.app.Application
import android.content.Context
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
