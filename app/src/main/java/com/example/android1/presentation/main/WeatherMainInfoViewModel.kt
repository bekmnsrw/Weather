package com.example.android1.presentation.main

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.android1.domain.geolocation.GeoLocation
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.example.android1.domain.weather.WeatherMainInfo
import com.example.android1.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

class WeatherMainInfoViewModel(
    private val getWeatherMainInfoUseCase: GetWeatherMainInfoUseCase,
    private val getCityIdUseCase: GetCityIdUseCase,
    private val getGeoLocationUseCase: GetGeoLocationUseCase
): ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _weatherDetailedInfo = MutableLiveData<List<WeatherMainInfo>>(null)
    val weatherDetailedInfo: LiveData<List<WeatherMainInfo>>
        get() = _weatherDetailedInfo

    private val _cityId = SingleLiveEvent<Int>()
    val cityId: SingleLiveEvent<Int>
        get() = _cityId

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: SingleLiveEvent<String>
        get() = _errorMessage

    private val _shouldShowAlertDialog = SingleLiveEvent<Boolean>()
    val shouldShowAlertDialog: SingleLiveEvent<Boolean>
        get() = _shouldShowAlertDialog

    private val _geoLocation = MutableLiveData<GeoLocation>(null)
    val geoLocation: LiveData<GeoLocation>
        get() = _geoLocation

    fun getUserLocation(arePermissionsGranted: Boolean) {
        getLocation(arePermissionsGranted)
    }

    private fun getLocation(arePermissionsGranted: Boolean) {
        viewModelScope.launch {
            try {
                getGeoLocationUseCase.invoke(arePermissionsGranted).also {
                    _geoLocation.value = it
                }
            } catch (error: Throwable) {
                _errorMessage.value = "Can't find your location"
                Timber.e(error.message)
            }
        }
    }

    fun getCityIdByName(cityName: String) {
        getCityId(cityName)
    }

    private fun getCityId(cityName: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                getCityIdUseCase.invoke(cityName).also {
                    _cityId.value = it
                }
            } catch (noInternetConnection: UnknownHostException) {
                _errorMessage.value = "Please, turn on Internet connection"
            } catch (httpException: HttpException) {
                _errorMessage.value = "Sorry, can't find city with such name"
            } catch (error: Throwable) {
                _errorMessage.value = "Sorry, something went wrong"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getNearbyCities(
        longitude: Double,
        latitude: Double,
        numberOfCities: Int,
        isLocal: Boolean
    ) {
        loadWeather(
            longitude,
            latitude,
            numberOfCities,
            isLocal
        )
    }

    private fun loadWeather(
        longitude: Double,
        latitude: Double,
        numberOfCities: Int,
        isLocal: Boolean
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                getWeatherMainInfoUseCase.invoke(
                    mapOf(
                        "lat" to "$latitude",
                        "lon" to "$longitude",
                        "cnt" to "$numberOfCities"
                    ),
                    isLocal
                ).also {
                    _weatherDetailedInfo.value = it
                }
            } catch (noInternetConnection: UnknownHostException) {
                _errorMessage.value = "Please, turn on Internet connection"
            } catch (error: Throwable) {
                _errorMessage.value = "Sorry, something went wrong"
            }
            finally {
                _loading.value = false
            }
        }
    }

    companion object {
        fun provideFactory(
            weatherMainInfoUseCase: GetWeatherMainInfoUseCase,
            cityIdUseCase: GetCityIdUseCase,
            geoLocationUseCase: GetGeoLocationUseCase
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WeatherMainInfoViewModel(weatherMainInfoUseCase, cityIdUseCase, geoLocationUseCase)
            }
        }
    }
}
