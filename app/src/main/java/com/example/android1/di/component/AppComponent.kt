package com.example.android1.di.component

import android.content.Context
import com.example.android1.di.module.GeoLocationModule
import com.example.android1.di.module.NetworkModule
import com.example.android1.di.module.WeatherModule
import com.example.android1.presentation.DetailedFragment
import com.example.android1.presentation.MainFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, WeatherModule::class, GeoLocationModule::class])
interface AppComponent {

    fun injectMainFragment(mainFragment: MainFragment)

    fun injectDetailedFragment(detailedFragment: DetailedFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder

        fun build(): AppComponent
    }
}
