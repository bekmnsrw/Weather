package com.example.android1.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android1.domain.geolocation.GeoLocation
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.example.android1.domain.weather.WeatherMainInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import retrofit2.adapter.rxjava3.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class WeatherMainInfoViewModel @Inject constructor(
    private val getWeatherMainInfoUseCase: GetWeatherMainInfoUseCase,
    private val getCityIdUseCase: GetCityIdUseCase,
    private val getGeoLocationUseCase: GetGeoLocationUseCase
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?>
        get() = _error

    private val _weatherDetailedInfo = MutableLiveData<List<WeatherMainInfo>>(null)
    val weatherDetailedInfo: LiveData<List<WeatherMainInfo>>
        get() = _weatherDetailedInfo

    private val _cityId = MutableLiveData<Int?>(null)
    val cityId: LiveData<Int?>
        get() = _cityId

    val showLocationAlertDialog = MutableLiveData(false)

    val showHttpError = MutableLiveData(false)

    val showInternetConnectionError = MutableLiveData(false)

    private val _geoLocation = MutableLiveData<GeoLocation>(null)
    val geoLocation: LiveData<GeoLocation>
        get() = _geoLocation

    private var weatherDisposable: CompositeDisposable = CompositeDisposable()
    private var geoLocationDisposable: Disposable? = null

    fun getUserLocation(arePermissionsGranted: Boolean) {
        getLocation(arePermissionsGranted)
    }

    private fun getLocation(arePermissionsGranted: Boolean) {
        geoLocationDisposable = getGeoLocationUseCase(arePermissionsGranted)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                _geoLocation.value = it
            }, onError = {
                _error.value = it
            })
    }

    fun getCityIdByName(cityName: String) {
        getCityId(cityName)
    }

    private fun getCityId(cityName: String) {
        weatherDisposable += getCityIdUseCase(cityName)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _loading.value = true }
            .doAfterTerminate { _loading.value = false }
            .subscribeBy(onSuccess = {
                _cityId.value = it
                _cityId.value = null
            }, onError = {
                when (it.javaClass) {
                    UnknownHostException::class.java -> showInternetConnectionError.value = false
                    HttpException::class.java -> showHttpError.value = false
                }
                _error.value = it
            })
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
        weatherDisposable += getWeatherMainInfoUseCase(
            mapOf(
                "lat" to "$latitude",
                "lon" to "$longitude",
                "cnt" to "$numberOfCities"
            ),
            isLocal
        )
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
        geoLocationDisposable?.dispose()
    }
}
