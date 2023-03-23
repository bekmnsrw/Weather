package com.example.android1.presentation.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.android1.domain.weather.GetWeatherDetailedInfoUseCase
import com.example.android1.domain.weather.WeatherDetailedInfo
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class WeatherDetailedInfoViewModel(
    private val getWeatherDetailedInfoUseCase: GetWeatherDetailedInfoUseCase
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

    fun getWeatherInCity(cityId: Int) {
        loadWeather(cityId)
    }

    private fun loadWeather(cityId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                getWeatherDetailedInfoUseCase.invoke(cityId).also { weather ->
                    _weatherDetailedInfo.value = weather
                }
            } catch (noInternetConnection: UnknownHostException) {
                _error.value = noInternetConnection
            } catch (error: Throwable) {
                _error.value = error
            } finally {
                _loading.value = false
            }
        }
    }

    companion object {
        fun provideFactory(
            weatherDetailedInfoUseCase: GetWeatherDetailedInfoUseCase
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WeatherDetailedInfoViewModel(weatherDetailedInfoUseCase)
            }
        }
    }
}
