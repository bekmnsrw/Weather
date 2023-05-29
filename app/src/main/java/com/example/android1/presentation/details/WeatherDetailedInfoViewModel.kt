package com.example.android1.presentation.details

import androidx.lifecycle.*
import com.example.android1.domain.weather.GetWeatherDetailedInfoUseCase
import com.example.android1.domain.weather.WeatherDetailedInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy

class WeatherDetailedInfoViewModel @AssistedInject constructor(
    private val getWeatherDetailedInfoUseCase: GetWeatherDetailedInfoUseCase,
    @Assisted private val cityId: Int
): ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?>
        get() = _error

    private val _weatherDetailedInfo = MutableLiveData<WeatherDetailedInfo>(null)
    val weatherDetailedInfo: LiveData<WeatherDetailedInfo>
        get() = _weatherDetailedInfo

    val showInternetConnectionError = MutableLiveData(false)

    private var weatherDisposable: CompositeDisposable = CompositeDisposable()

    fun getWeatherInCity() {
        loadWeather()
    }

    private fun loadWeather() {
        weatherDisposable += getWeatherDetailedInfoUseCase(cityId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _loading.value = true }
            .doAfterTerminate { _loading.value = false }
            .subscribeBy(onSuccess = {
                _weatherDetailedInfo.value = it
            }, onError = {
                _error.value = it
            })
    }

    override fun onCleared() {
        super.onCleared()
        weatherDisposable.clear()
    }

    @AssistedFactory
    interface WeatherDetailedInfoViewModelFactory {

        fun create(cityId: Int): WeatherDetailedInfoViewModel
    }

    companion object {

        fun provideFactory(
            assistedFactory: WeatherDetailedInfoViewModelFactory,
            cityId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T = assistedFactory.create(cityId) as T
        }
    }
}
