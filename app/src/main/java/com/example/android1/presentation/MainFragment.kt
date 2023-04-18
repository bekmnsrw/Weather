package com.example.android1.presentation

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
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android1.R
import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache.cache
import com.example.android1.databinding.FragmentMainBinding
import com.example.android1.presentation.viewmodel.WeatherMainInfoViewModel
import com.example.android1.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import retrofit2.adapter.rxjava3.HttpException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private var viewBinding: FragmentMainBinding? = null

    private val viewModel: WeatherMainInfoViewModel by viewModels()

    private var adapter: WeatherListAdapter? = null

    private var longitude: Double = DEFAULT_LONGITUDE
    private var latitude: Double = DEFAULT_LATITUDE

    private var searchDisposable: Disposable? = null

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

    override fun onStart() {
        super.onStart()
        searchCity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentMainBinding.bind(view)
        initAdapter()
        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        searchDisposable?.dispose()
    }

    private fun suggestUserToTurnOnGPS() {
        viewModel.showLocationAlertDialog.observe(viewLifecycleOwner) {
            if (it == false) {
                showAlertDialog(requireContext())
                viewModel.showLocationAlertDialog.value = true
            }
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

    private fun manageGeoLocation(newLongitude: Double?, newLatitude: Double?) {
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

            error.observe(viewLifecycleOwner) {
                if (it == null) return@observe

                when (it.javaClass) {
                    UnknownHostException::class.java -> {
                        if (showInternetConnectionError.value == false) {
                            showError(getString(R.string.internet_connection_error_message))
                            showInternetConnectionError.value = true
                        }
                    }

                    HttpException::class.java -> {
                        if (showHttpError.value == false) {
                            showError(getString(R.string.http_error_message))
                            showHttpError.value = true
                        }
                    }

                    else ->
                        showError(getString(R.string.general_error_message))
                }
            }

            cityId.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                navigateOnDetailedFragment(it)
                viewBinding?.search?.setQuery(null, true)
            }

            geoLocation.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                manageGeoLocation(it.longitude, it.latitude)
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

    private fun SearchView.observeQuery(): Flowable<String> =
        Flowable.create({ emitter ->
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    emitter.onNext(newText.toString())
                    return false
                }
            })
        }, BackpressureStrategy.LATEST)

    private fun searchCity() {
        viewBinding?.run {
            searchDisposable?.dispose()
            searchDisposable = search.observeQuery()
                .debounce(500L, TimeUnit.MILLISECONDS)
                .filter { it.length > 2 }
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    viewModel.getCityIdByName(it)
                }, onError = {
                    showError(getString(R.string.general_error_message))
                })
        }
    }

    private fun navigateOnDetailedFragment(cityId: Int) {
        val action = MainFragmentDirections.actionMainFragmentToDetailedFragment(cityId)
        findNavController().navigate(action)
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
    }
}
