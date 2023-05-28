package com.example.android1.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android1.domain.weather.GetWeatherDetailedInfoUseCase
import com.example.android1.domain.weather.WeatherDetailedInfo
import com.example.android1.utils.getOrAwaitValue
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDetailedInfoViewModelTest {

    @MockK
    lateinit var getWeatherDetailedInfoUseCase: GetWeatherDetailedInfoUseCase

    private val cityId: Int = 1

    private lateinit var viewModel: WeatherDetailedInfoViewModel

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
        viewModel = WeatherDetailedInfoViewModel(
            getWeatherDetailedInfoUseCase = getWeatherDetailedInfoUseCase,
            cityId = cityId
        )
    }

    @Test
    fun `When call getWeatherInCity, expect success`() {
        val mockWeather = mockk<WeatherDetailedInfo> {
            every { cityName } returns "Kazan"
            every { temperature } returns 25.5
            every { feelsLike } returns 26.0
            every { humidity } returns 12
            every { pressure } returns 12
            every { windDegree } returns 12
            every { windSpeed } returns 12.2
            every { sunriseTime } returns 123_456_789
            every { sunsetTime } returns 987_654_321
            every { weatherIcon } returns "test"
            every { weatherDescription } returns "Sunny"
        }

        val expectedWeather = WeatherDetailedInfo(
            cityName = "Kazan",
            temperature = 25.5,
            feelsLike = 26.0,
            humidity = 12,
            pressure = 12,
            windDegree = 12,
            windSpeed = 12.2,
            sunriseTime = 123_456_789,
            sunsetTime = 987_654_321,
            weatherIcon = "test",
            weatherDescription = "Sunny"
        )

        coEvery {
            getWeatherDetailedInfoUseCase.invoke(any())
        } returns mockWeather

        viewModel.getWeatherInCity()

        assertEquals(expectedWeather, viewModel.weatherDetailedInfo.getOrAwaitValue())
        assertTrue(viewModel.error.value == null)
    }

    @Test
    fun `When call getWeatherInCity and no Internet-connection, expect UnknownHostException being threw`() {
        coEvery {
            getWeatherDetailedInfoUseCase.invoke(any())
        } throws UnknownHostException()

        viewModel.getWeatherInCity()

        assertTrue(viewModel.error.value is UnknownHostException)
    }

    @Test
    fun `When call getWeatherInCity, expect Throwable`() {
        coEvery {
            getWeatherDetailedInfoUseCase.invoke(any())
        } throws Throwable()

        viewModel.getWeatherInCity()

        assertTrue(viewModel.error.value is Throwable)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
