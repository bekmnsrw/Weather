package com.example.android1

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.example.android1.di.component.AppComponent
import com.example.android1.di.component.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent.builder()
            .applicationContext(applicationContext = applicationContext)
            .build()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}
