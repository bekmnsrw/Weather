package com.example.android1.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.android1.R
import com.example.android1.data.DataContainer
import com.example.android1.data.WeatherUIModel
import com.example.android1.data.WeatherUiModelsCache.weatherUIModels
import com.example.android1.databinding.FragmentMainBinding
import com.example.android1.utils.showSnackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainFragment : Fragment(R.layout.fragment_main) {

    private var viewBinding: FragmentMainBinding? = null
    private val api = DataContainer.weatherApi
    private var adapter: WeatherListAdapter? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var longitude: Double = DEFAULT_LONGITUDE
    private var latitude: Double = DEFAULT_LATITUDE

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                if (isLocationEnabled()) {
                    getCurrentLocation()
                } else {
                    suggestUserToTurnOnGPS(requireContext())
                }
            } else {
                getNearbyCities(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)
            }
        }

    override fun onResume() {
        super.onResume()

        when {
            !checkPermissions() && weatherUIModels.isEmpty() -> {
                return
            }

            !checkPermissions() && weatherUIModels.isNotEmpty() -> {
                showNearbyCities()
            }

            !isLocationEnabled() && weatherUIModels.isEmpty() -> {
                getNearbyCities(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)
            }

            checkPermissions() && isLocationEnabled() && weatherUIModels.isNotEmpty() -> {
                getCurrentLocation()
            }

            !isLocationEnabled() && weatherUIModels.isNotEmpty() ||
            !checkPermissions() && weatherUIModels.isNotEmpty() -> {
                showNearbyCities()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        requestLocationPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)
        initAdapter()
        onSearchClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
            if (it != null) {
                if (it.longitude != longitude || it.latitude != latitude) {
                    longitude = it.longitude
                    latitude = it.latitude
                    weatherUIModels = mutableListOf()
                    getNearbyCities(longitude, latitude)
                } else {
                    showNearbyCities()
                }
            }
        }
    }

    private fun showNearbyCities() {
        adapter?.submitList(weatherUIModels)
    }

    private fun getNearbyCities(longitude: Double, latitude: Double) {
        lifecycleScope.launch {
            try {
                showLoading(true)

                weatherUIModels = mutableListOf()
                api.getWeatherInNearbyCities(
                    mapOf(
                        "lat" to "$latitude",
                        "lon" to "$longitude",
                        "cnt" to "$NUMBER_OF_CITIES"
                    )
                ).also {
                    it.list.forEach { city ->
                        weatherUIModels.add(
                            WeatherUIModel(
                                city.id,
                                city.weather.first().icon,
                                city.name,
                                city.main.temp,
                                calculateColor(city.main.temp.roundToInt())
                            )
                        )
                    }
                }
            } catch (error: Throwable) {
                showError()
            } finally {
                showNearbyCities()
                showLoading(false)
            }
        }
    }

    private fun getCityIdByName(city: String) {
        lifecycleScope.launch {
            try {
                showLoading(true)
                navigateOnDetailedFragment(api.getCityId(city).id)
            } catch (error: Throwable) {
                showError()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun initAdapter() {
        viewBinding?.run {
            val itemDecorator = CustomItemDecorator(requireContext(), RV_SPACING)
            adapter = WeatherListAdapter(::onItemClick)
            rvCities.adapter = adapter
            rvCities.addItemDecoration(itemDecorator)
        }
    }

    private fun onItemClick(id: Int) {
        navigateOnDetailedFragment(id)
    }

    private fun onSearchClick() {
        viewBinding?.run {
            search.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    getCityIdByName(query.toString())
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun navigateOnDetailedFragment(cityId: Int) {
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .replace(R.id.container, DetailedFragment.createBundle(cityId))
            .addToBackStack(MainActivity.MAIN_FRAGMENT_TAG)
            .commit()
    }

    private fun showLoading(isShow: Boolean) {
        viewBinding?.progress?.isVisible = isShow
    }

    private fun showError() {
        requireActivity()
            .findViewById<View>(android.R.id.content)
            .showSnackbar(ERROR_MESSAGE)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkPermissions(): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun calculateColor(temperature: Int): Int {
        var color: Int = R.color.black

        when {
            temperature < -30 -> {
                color = R.color.violet
            }
            temperature in -30..-14 -> {
                color = R.color.dark_blue
            }
            temperature in -15..0 -> {
                color = R.color.blue
            }
            temperature in 1..15 -> {
                color = R.color.green
            }
            temperature in 16..30 -> {
                color = R.color.yellow
            }
            temperature > 30 -> {
                color = R.color.orange
            }
        }

        return color
    }

    private fun suggestUserToTurnOnGPS(context: Context) {
        val locationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

        AlertDialog.Builder(context)
            .setTitle(ALERT_DIALOG_TITLE)
            .setMessage(ALERT_DIALOG_MESSAGE)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(context, locationIntent, null)
            }
            .setNeutralButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        private const val DEFAULT_LONGITUDE = 10.0
        private const val DEFAULT_LATITUDE = 10.0
        private const val RV_SPACING = 16.0F
        private const val NUMBER_OF_CITIES = 12

        private const val ERROR_MESSAGE = "Sorry, city wasn't found."
        private const val ALERT_DIALOG_TITLE = "Location tracking is turned off"
        private const val ALERT_DIALOG_MESSAGE =
            "Can't get your current location to show relevant weather data. " +
                    "You can provide location access in app settings. \n\n" +
                    "Would you like to open app settings?"
    }
}
