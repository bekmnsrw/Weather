package com.example.android1.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.android1.R
import com.example.android1.data.DataContainer
import com.example.android1.databinding.FragmentDetailedBinding
import com.example.android1.utils.showSnackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class DetailedFragment : Fragment(R.layout.fragment_detailed) {

    private var viewBinding: FragmentDetailedBinding? = null
    private var cityId: Int? = null
    private val api = DataContainer.weatherApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.arguments?.let {
            cityId = it.getInt(CITY_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentDetailedBinding.bind(view)

        viewBinding?.run {
            cityId?.let {
                loadWeather(it)
            }

            btnIconBack.setOnClickListener {
                navigateOnMainFragment()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    private fun loadWeather(id: Int) {
        lifecycleScope.launch {
            try {
                showLoading(true)

                api.getWeather(id).also { weatherResponse ->
                    showCityName(weatherResponse.name)

                    showTemperature(weatherResponse.main.temp)
                    showFeelsLike(weatherResponse.main.feelsLike)

                    showHumidity(weatherResponse.main.humidity)
                    showPressure(weatherResponse.main.pressure)

                    showWindDirection(weatherResponse.wind.deg)
                    showWindowSpeed(weatherResponse.wind.speed)

                    showSunriseTime(weatherResponse.sys.sunrise)
                    showSunsetTime(weatherResponse.sys.sunset)

                    weatherResponse.weather.also {
                        it.firstOrNull()?.also { weather ->
                            showWeatherDescription(weather.main)
                            showWeatherIcon(weather.icon)
                        }
                    }
                }
            } catch (error: Throwable) {
                showError()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun navigateOnMainFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun showError() {
        requireActivity()
            .findViewById<View>(android.R.id.content)
            .showSnackbar(ERROR_MESSAGE)
    }

    private fun showTemperature(temperature: Double) {
        viewBinding?.tvTemperature?.text = "${temperature.roundToInt()}°"
    }

    private fun showCityName(cityName: String) {
        viewBinding?.tvCity?.text = cityName
    }

    private fun showHumidity(humidity: Int) {
        viewBinding?.tvHumidityDescription?.text = "$humidity%"
    }

    private fun showWindDirection(degree: Int) {
        val windDirection = convertWindAngleIntoDirection(degree)
        viewBinding?.tvWindDirectionDescription?.text = windDirection
    }

    private fun convertWindAngleIntoDirection(angle: Int): String {
        val index = (angle / 45).toDouble().roundToInt() % 8
        return windDirections[index]
    }

    private fun showFeelsLike(feelsLike: Double) {
        viewBinding?.tvFeelsLike?.text = "Feels like: ${feelsLike.roundToInt()}°"
    }

    private fun showPressure(pressure: Int) {
        val pressureInHPa = convertPressureIntoMmHg(pressure)
        viewBinding?.tvPressureDescription?.text = "$pressureInHPa mmHg"
    }

    private fun showWeatherDescription(weather: String) {
        viewBinding?.tvWeatherDescription?.text = weather
    }

    private fun convertPressureIntoMmHg(hPa: Int): Int {
        return (hPa * PRESSURE_CONSTANT).roundToInt()
    }

    private fun showWeatherIcon(iconId: String) {
        viewBinding?.ivWeather?.load("https://openweathermap.org/img/w/$iconId.png")
    }

    private fun showWindowSpeed(windowSpeed: Double) {
        viewBinding?.tvWindSpeedDescription?.text = "${windowSpeed} m/s"
    }

    private fun showSunriseTime(sunrise: Int) {
        val sunriseTime = convertMillisecondsToHoursAndMinutes(sunrise)
        viewBinding?.tvSunriseDescription?.text = sunriseTime
    }

    private fun showSunsetTime(sunset: Int) {
        val sunsetTime = convertMillisecondsToHoursAndMinutes(sunset)
        viewBinding?.tvSunsetDescription?.text = sunsetTime
    }

    private fun convertMillisecondsToHoursAndMinutes(milliseconds: Int): String {
        return SimpleDateFormat(TIME_PATTERN).format(milliseconds.toLong() * TIME_CONSTANT)
    }

    private fun showLoading(isShow: Boolean) {
        viewBinding?.progress?.isVisible = isShow
    }

    companion object {
        private const val CITY_ID = "CITY_ID"
        private const val TIME_PATTERN = "HH:mm"
        private const val ERROR_MESSAGE = "Sorry, something went wrong"
        private const val TIME_CONSTANT = 1000L
        private const val PRESSURE_CONSTANT = 0.75

        private val windDirections = arrayOf(
            "North ↓", "North-East ↙", "East ←", "South-East ↖",
            "South ↑", "South-West ↗", "West →", "North-West ↘"
        )

        fun createBundle(cityId: Int) = DetailedFragment().apply {
            arguments = Bundle().apply {
                putInt(CITY_ID, cityId)
            }
        }
    }
}
