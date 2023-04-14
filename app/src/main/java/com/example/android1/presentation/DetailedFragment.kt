package com.example.android1.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.android1.R
import com.example.android1.databinding.FragmentDetailedBinding
import com.example.android1.presentation.viewmodel.WeatherDetailedInfoViewModel
import com.example.android1.utils.convertMillisecondsToHoursAndMinutes
import com.example.android1.utils.convertPressureIntoMmHg
import com.example.android1.utils.convertWindAngleIntoDirection
import com.example.android1.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class DetailedFragment : Fragment(R.layout.fragment_detailed) {

    private var viewBinding: FragmentDetailedBinding? = null

    private val args: DetailedFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: WeatherDetailedInfoViewModel.WeatherDetailedInfoViewModelFactory

    private val viewModel: WeatherDetailedInfoViewModel by viewModels {
        WeatherDetailedInfoViewModel.provideFactory(viewModelFactory, args.cityId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentDetailedBinding.bind(view)

        if (args.cityId != -1) {
            viewModel.getWeatherInCity()
        }

        observeViewModel()

        viewBinding?.btnIconBack?.setOnClickListener {
            findNavController()
                .navigate(R.id.action_detailedFragment_to_mainFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    private fun observeViewModel() {
        with(viewModel) {
            loading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            weatherDetailedInfo.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                showCityName(it.cityName)
                showTemperature(it.temperature)
                showFeelsLike(it.feelsLike)
                showHumidity(it.humidity)
                showPressure(it.pressure)
                showWindDirection(it.windDegree)
                showWindSpeed(it.windSpeed)
                showSunriseTime(it.sunriseTime)
                showSunsetTime(it.sunsetTime)
                showWeatherDescription(it.weatherDescription)
                showWeatherIcon(it.weatherIcon)
            }

            error.observe(viewLifecycleOwner) {
                if (it == null) return@observe

                if (it.javaClass == UnknownHostException::class.java) {
                    showError(getString(R.string.internet_connection_error_message))
                } else {
                    showError(getString(R.string.general_error_message))
                }
            }
        }
    }

    private fun showTemperature(temperature: Double) {
        viewBinding?.tvTemperature?.text =
            getString(R.string.temperature, temperature.roundToInt())
    }

    private fun showCityName(cityName: String) {
        viewBinding?.tvCity?.text = cityName
    }

    private fun showHumidity(humidity: Int) {
        viewBinding?.tvHumidityDescription?.text =
            getString(R.string.humidity, humidity)
    }

    private fun showWindDirection(degree: Int) {
        viewBinding?.tvWindDirectionDescription?.text =
            degree.convertWindAngleIntoDirection()
    }

    private fun showFeelsLike(feelsLike: Double) {
        viewBinding?.tvFeelsLike?.text =
            getString(R.string.feels_like, feelsLike.roundToInt())
    }

    private fun showPressure(pressure: Int) {
        viewBinding?.tvPressureDescription?.text =
            getString(R.string.pressure, pressure.convertPressureIntoMmHg())
    }

    private fun showWeatherDescription(weatherDescription: String) {
        viewBinding?.tvWeatherDescription?.text = weatherDescription
    }

    private fun showWeatherIcon(iconId: String) {
        viewBinding?.ivWeather?.load(getString(R.string.icon_load_path, iconId))
    }

    private fun showWindSpeed(windSpeed: Double) {
        viewBinding?.tvWindSpeedDescription?.text =
            getString(R.string.wind_speed, windSpeed.roundToInt())
    }

    private fun showSunriseTime(sunrise: Int) {
        viewBinding?.tvSunriseDescription?.text =
            sunrise.convertMillisecondsToHoursAndMinutes()
    }

    private fun showSunsetTime(sunset: Int) {
        viewBinding?.tvSunsetDescription?.text =
            sunset.convertMillisecondsToHoursAndMinutes()
    }

    private fun showLoading(isShow: Boolean) {
        viewBinding?.progress?.isVisible = isShow
    }

    private fun showError(message: String) {
        requireActivity()
            .findViewById<View>(android.R.id.content)
            .showSnackbar(message)
    }
}
