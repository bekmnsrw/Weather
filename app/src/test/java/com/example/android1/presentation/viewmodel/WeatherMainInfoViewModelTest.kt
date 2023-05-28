package com.example.android1.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.android1.domain.geolocation.GeoLocation
import com.example.android1.domain.geolocation.GetGeoLocationUseCase
import com.example.android1.domain.weather.GetCityIdUseCase
import com.example.android1.domain.weather.GetWeatherMainInfoUseCase
import com.example.android1.domain.weather.WeatherMainInfo
import com.example.android1.utils.getOrAwaitValue
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
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
import kotlin.test.assertContentEquals

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

    @RelaxedMockK
    private lateinit var loadingObserver: Observer<Boolean>

    private lateinit var loadingHistory: MutableList<Boolean>

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
        viewModel = WeatherMainInfoViewModel(
            getWeatherMainInfoUseCase = getWeatherMainInfoUseCase,
            getCityIdUseCase = getCityIdUseCase,
            getGeoLocationUseCase = getGeoLocationUseCase
        )
        loadingHistory = mutableListOf()
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
        assertTrue(viewModel.error.getOrAwaitValue() == null)
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
        assertTrue(viewModel.error.getOrAwaitValue() == null)
    }

    @Test
    fun `When call geoLocation, expect Throwable`() {
        coEvery {
            getGeoLocationUseCase.invoke(true)
        } throws Throwable()

        viewModel.getUserLocation(true)

        assertTrue(viewModel.error.getOrAwaitValue() is Throwable)
        assertTrue(viewModel.geoLocation.getOrAwaitValue() == null)
    }

    @Test
    fun `When call getCityId, expect city ID`() {
        val mockCityId: Int = 1
        val expectedCityId: Int = 1
        val requestCityName: String = "Kazan"

        val slot = slot<Boolean>()

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } returns mockCityId

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getCityIdByName(requestCityName)

        assertEquals(expectedCityId, viewModel.cityId.getOrAwaitValue())
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.error.getOrAwaitValue() == null)
    }

    @Test
    fun `When call getCityId and no Internet-connection, expect UnknownHostException being threw`() {
        val requestCityName: String = "Kazan"

        val slot = slot<Boolean>()

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } throws UnknownHostException()

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getCityIdByName(requestCityName)

        assertTrue(viewModel.error.getOrAwaitValue() is UnknownHostException)
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.cityId.getOrAwaitValue() == null)
    }

    @Test
    fun `When call getCityId and no such city, expect HttpException being threw`() {
        val requestCityName: String = "owefoewfjnewofkn"

        val slot = slot<Boolean>()

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } throws HttpException(
            Response.error<ResponseBody>(
                404,
                "No such city".toResponseBody()
            )
        )

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getCityIdByName(requestCityName)

        assertTrue(viewModel.error.getOrAwaitValue() is HttpException)
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.cityId.getOrAwaitValue() == null)
    }

    @Test
    fun `When call getCityId, expect Throwable`() {
        val requestCityName: String = "Kazan"

        val slot = slot<Boolean>()

        coEvery {
            getCityIdUseCase.invoke(requestCityName)
        } throws Throwable()

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getCityIdByName(requestCityName)

        assertTrue(viewModel.error.getOrAwaitValue() is Throwable)
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.cityId.getOrAwaitValue() == null)
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

        val slot = slot<Boolean>()

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

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getNearbyCities(
            longitude = requestLongitude,
            latitude = requestLatitude,
            numberOfCities = requestNumberOfCities,
            isLocal = requestIsLocal
        )

        assertEquals(expectedWeatherMainInfoList, viewModel.weatherDetailedInfo.getOrAwaitValue())
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.error.getOrAwaitValue() == null)
    }

    @Test
    fun `When call getNearbyCities and no Internet-connection, expect UnknownHostException being threw`() {
        val requestLongitude = 12.2
        val requestLatitude = 12.2
        val requestNumberOfCities = 2
        val requestIsLocal = true

        val slot = slot<Boolean>()

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

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getNearbyCities(
            longitude = requestLongitude,
            latitude = requestLatitude,
            numberOfCities = requestNumberOfCities,
            isLocal = requestIsLocal
        )

        assertTrue(viewModel.error.getOrAwaitValue() is UnknownHostException)
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.weatherDetailedInfo.getOrAwaitValue() == null)
    }

    @Test
    fun `When call getNearbyCities, expect Throwable`() {
        val requestLongitude = 12.2
        val requestLatitude = 12.2
        val requestNumberOfCities = 2
        val requestIsLocal = true

        val slot = slot<Boolean>()

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

        viewModel.loading.observeForever(loadingObserver)

        every {
            loadingObserver.onChanged(capture(slot))
        } answers {
            loadingHistory.add(slot.captured)
        }

        viewModel.getNearbyCities(
            longitude = requestLongitude,
            latitude = requestLatitude,
            numberOfCities = requestNumberOfCities,
            isLocal = requestIsLocal
        )

        assertTrue(viewModel.error.getOrAwaitValue() is Throwable)
        assertContentEquals(arrayListOf(true, false), loadingHistory)
        assertTrue(viewModel.weatherDetailedInfo.getOrAwaitValue() == null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
