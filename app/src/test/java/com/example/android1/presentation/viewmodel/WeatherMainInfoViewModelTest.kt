package com.example.android1.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android1.domain.geolocation.GeoLocation
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.example.android1.domain.weather.WeatherMainInfo
import com.example.android1.utils.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherMainInfoViewModelTest {

    @MockK
    lateinit var getWeatherMainInfoUseCase: GetWeatherMainInfoUseCase

    @MockK
    lateinit var getCityIdUseCase: GetCityIdUseCase

    @MockK
    lateinit var getGeoLocationUseCase: GetGeoLocationUseCase

    private lateinit var viewModel: WeatherMainInfoViewModel

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
        viewModel = WeatherMainInfoViewModel(
            getWeatherMainInfoUseCase = getWeatherMainInfoUseCase,
            getCityIdUseCase = getCityIdUseCase,
            getGeoLocationUseCase = getGeoLocationUseCase
        )
    }

    @Test
    fun `When call geoLocation and permissions are granted, expect GeoLocation`() {
        val mockGeoLocation = mockk<GeoLocation> {
            every { longitude } returns 12.2
            every { latitude } returns 12.2
        }

        val expectedGeoLocation = GeoLocation(
            longitude = 12.2,
            latitude = 12.2
        )

        val requestArePermissionsGranted = true

        coEvery {
            getGeoLocationUseCase.invoke(requestArePermissionsGranted)
        } returns mockGeoLocation

        viewModel.getUserLocation(requestArePermissionsGranted)

        assertEquals(expectedGeoLocation, viewModel.geoLocation.getOrAwaitValue())
    }

    @Test
    fun `When call geoLocation and permissions aren't granted, expect GeoLocation with null values`() {
        val mockGeoLocation = mockk<GeoLocation> {
            every { longitude } returns null
            every { latitude } returns null
        }

        val expectedGeoLocation = GeoLocation(
            longitude = null,
            latitude = null
        )

        val requestArePermissionsGranted = false

        coEvery {
            getGeoLocationUseCase.invoke(requestArePermissionsGranted)
        } returns mockGeoLocation

        viewModel.getUserLocation(requestArePermissionsGranted)

        assertEquals(expectedGeoLocation, viewModel.geoLocation.getOrAwaitValue())
    }

    @Test
    fun `When call geoLocation, expect Throwable`() {
        coEvery {
            getGeoLocationUseCase.invoke(true)
        } throws Throwable()

        viewModel.getUserLocation(true)

        assertTrue(viewModel.error.getOrAwaitValue() is Throwable)
    }

    @Test
    fun `When call getCityId, expect city ID`() {
        val mockCityId: Int = 1
        val expectedCityId: Int = 1
        val requestCityName: String = "Kazan"

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } returns mockCityId

        viewModel.getCityIdByName(requestCityName)

        assertEquals(expectedCityId, viewModel.cityId.getOrAwaitValue())
    }

    @Test
    fun `When call getCityId and no Internet-connection, expect UnknownHostException being threw`() {
        val requestCityName: String = "Kazan"

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } throws UnknownHostException()

        viewModel.getCityIdByName(requestCityName)

        assertTrue(viewModel.error.getOrAwaitValue() is UnknownHostException)
    }

    @Test
    fun `When call getCityId and no such city, expect HttpException being threw`() {
        val requestCityName: String = "owefoewfjnewofkn"

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } throws HttpException(
            Response.error<ResponseBody>(
                404,
                "No such city".toResponseBody()
            )
        )

        viewModel.getCityIdByName(requestCityName)

        assertTrue(viewModel.error.getOrAwaitValue() is HttpException)
    }

    @Test
    fun `When call getCityId, expect Throwable`() {
        val requestCityName: String = "Kazan"

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } throws Throwable()

        viewModel.getCityIdByName(requestCityName)

        assertTrue(viewModel.error.getOrAwaitValue() is Throwable)
    }

    @Test
    fun `When call getNearbyCities, expect list of WeatherMainInfo`() {
        val mockWeatherMainInfoList: List<WeatherMainInfo> = arrayListOf(
            mockk {
                every { cityId } returns 1
                every { weatherIcon } returns "test1"
                every { cityName } returns "Kazan"
                every { temperature } returns 25.5
                every { temperatureTextViewColor } returns 1
            },
            mockk {
                every { cityId } returns 2
                every { weatherIcon } returns "test2"
                every { cityName } returns "Moscow"
                every { temperature } returns 26.0
                every { temperatureTextViewColor } returns 2
            }
        )

        val expectedWeatherMainInfoList: List<WeatherMainInfo> = arrayListOf(
            WeatherMainInfo(
                cityId = 1,
                weatherIcon = "test1",
                cityName = "Kazan",
                temperature = 25.5,
                temperatureTextViewColor = 1
            ),
            WeatherMainInfo(
                cityId = 2,
                weatherIcon = "test2",
                cityName = "Moscow",
                temperature = 26.0,
                temperatureTextViewColor = 2
            )
        )

        val requestLongitude = 12.2
        val requestLatitude = 12.2
        val requestNumberOfCities = 2
        val requestIsLocal = true

        coEvery {
            getWeatherMainInfoUseCase.invoke(
                query = mapOf(
                    "lat" to "$requestLatitude",
                    "lon" to "$requestLongitude",
                    "cnt" to "$requestNumberOfCities"
                ),
                isLocal = requestIsLocal
            )
        } returns mockWeatherMainInfoList

        viewModel.getNearbyCities(
            longitude = requestLongitude,
            latitude = requestLatitude,
            numberOfCities = requestNumberOfCities,
            isLocal = requestIsLocal
        )

        assertEquals(expectedWeatherMainInfoList, viewModel.weatherDetailedInfo.getOrAwaitValue())
    }

    @Test
    fun `When call getNearbyCities and no Internet-connection, expect UnknownHostException being threw`() {
        val requestLongitude = 12.2
        val requestLatitude = 12.2
        val requestNumberOfCities = 2
        val requestIsLocal = true

        coEvery {
            getWeatherMainInfoUseCase.invoke(
                query = mapOf(
                    "lat" to "$requestLatitude",
                    "lon" to "$requestLongitude",
                    "cnt" to "$requestNumberOfCities"
                ),
                isLocal = requestIsLocal
            )
        } throws UnknownHostException()

        viewModel.getNearbyCities(
            longitude = requestLongitude,
            latitude = requestLatitude,
            numberOfCities = requestNumberOfCities,
            isLocal = requestIsLocal
        )

        assertTrue(viewModel.error.getOrAwaitValue() is UnknownHostException)
    }

    @Test
    fun `When call getNearbyCities, expect Throwable`() {
        val requestLongitude = 12.2
        val requestLatitude = 12.2
        val requestNumberOfCities = 2
        val requestIsLocal = true

        coEvery {
            getWeatherMainInfoUseCase.invoke(
                query = mapOf(
                    "lat" to "$requestLatitude",
                    "lon" to "$requestLongitude",
                    "cnt" to "$requestNumberOfCities"
                ),
                isLocal = requestIsLocal
            )
        } throws Throwable()

        viewModel.getNearbyCities(
            longitude = requestLongitude,
            latitude = requestLatitude,
            numberOfCities = requestNumberOfCities,
            isLocal = requestIsLocal
        )

        assertTrue(viewModel.error.getOrAwaitValue() is Throwable)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
