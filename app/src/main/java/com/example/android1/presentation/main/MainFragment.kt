package com.example.android1.presentation.main

import android.Manifest
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android1.App
import com.example.android1.R
import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache.cache
import com.example.android1.databinding.FragmentMainBinding
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.example.android1.presentation.details.CustomItemDecorator
import com.example.android1.presentation.details.WeatherListAdapter
import com.example.android1.utils.showSnackbar
import javax.inject.Inject

class MainFragment : Fragment(R.layout.fragment_main) {

    private var viewBinding: FragmentMainBinding? = null

    @Inject
    lateinit var weatherMainInfoUseCase: GetWeatherMainInfoUseCase

    @Inject
    lateinit var cityIdUseCase: GetCityIdUseCase

    @Inject
    lateinit var geoLocationUseCase: GetGeoLocationUseCase

    private val viewModel: WeatherMainInfoViewModel by viewModels {
        WeatherMainInfoViewModel.provideFactory(
            weatherMainInfoUseCase,
            cityIdUseCase,
            geoLocationUseCase
        )
    }

    private var adapter: WeatherListAdapter? = null

    private var longitude: Double = DEFAULT_LONGITUDE
    private var latitude: Double = DEFAULT_LATITUDE

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                if (isLocationEnabled()) {
                    viewModel.getUserLocation(true)
                } else {
                    suggestUserToTurnOnGPS()
                }
            } else {
                showNearbyCities(DEFAULT_LONGITUDE, DEFAULT_LATITUDE, false)
            }
        }

    override fun onResume() {
        super.onResume()

        when {
            !checkPermissions() && cache.isEmpty() -> return

            !checkPermissions() && cache.isNotEmpty() ->
                showNearbyCities(DEFAULT_LONGITUDE, DEFAULT_LATITUDE, true)

            !isLocationEnabled() && cache.isEmpty() ->
                showNearbyCities(DEFAULT_LONGITUDE, DEFAULT_LATITUDE, false)

            checkPermissions() && isLocationEnabled() && cache.isNotEmpty() ->
                viewModel.getUserLocation(true)

            cache.isNotEmpty() && (!isLocationEnabled() || !checkPermissions()) ->
                showNearbyCities(DEFAULT_LONGITUDE, DEFAULT_LATITUDE, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.injectMainFragment(this)
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)
        initAdapter()
        observeViewModel()
        onSearchClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    private fun suggestUserToTurnOnGPS() {
        viewModel.shouldShowAlertDialog.observe(viewLifecycleOwner) {
            showAlertDialog(requireContext())
        }
    }

    private fun showNearbyCities(
        longitude: Double,
        latitude: Double,
        isLocal: Boolean
    ) {
        viewModel.getNearbyCities(
            longitude,
            latitude,
            NUMBER_OF_CITIES,
            isLocal
        )
    }

    private fun getLocation(newLongitude: Double?, newLatitude: Double?) {
        if (newLongitude != null && newLatitude != null) {
            if (newLongitude != longitude || newLatitude != latitude) {
                longitude = newLongitude
                latitude = newLatitude
                showNearbyCities(longitude, latitude, false)
            } else {
                showNearbyCities(longitude, latitude, true)
            }
        } else {
            showError(getString(R.string.geo_location_error_message))
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            loading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            weatherDetailedInfo.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                adapter?.submitList(it)
            }

            errorMessage.observe(viewLifecycleOwner) {
                showError(it)
            }

            cityId.observe(viewLifecycleOwner) {
                navigateOnDetailedFragment(it)
            }

            geoLocation.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                getLocation(it.longitude, it.latitude)
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
                    if (query != null) {
                        viewModel.getCityIdByName(query.toString()).also {
                            search.setQuery(null, true)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
    }

    private fun navigateOnDetailedFragment(cityId: Int) {
        val bundle = Bundle()
        bundle.putInt(CITY_ID, cityId)

        findNavController()
            .navigate(R.id.action_mainFragment_to_detailedFragment, bundle)
    }

    private fun showLoading(isShow: Boolean) {
        viewBinding?.progress?.isVisible = isShow
    }

    private fun showError(message: String) {
        requireActivity()
            .findViewById<View>(android.R.id.content)
            .showSnackbar(message)
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

    private fun showAlertDialog(context: Context) {
        val locationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

        AlertDialog.Builder(context)
            .setTitle(getString(R.string.alert_dialog_title))
            .setMessage(getString(R.string.alert_dialog_message))
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
        private const val NUMBER_OF_CITIES = 10

        const val CITY_ID = "CITY_ID"
    }
}
