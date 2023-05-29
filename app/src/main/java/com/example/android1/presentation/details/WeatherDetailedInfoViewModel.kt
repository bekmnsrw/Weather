package com.example.android1.presentation.details

import androidx.lifecycle.*
import com.example.android1.domain.weather.GetWeatherDetailedInfoUseCase
import com.example.android1.domain.weather.WeatherDetailedInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.net.UnknownHostException

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

    fun getWeatherInCity() {
        loadWeather()
    }

    private fun loadWeather() {
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
