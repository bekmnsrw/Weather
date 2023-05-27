package com.example.android1.domain.weather

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class GetWeatherDetailedInfoUseCaseTest {

    @MockK
    lateinit var weatherRepository: WeatherRepository

    private lateinit var getWeatherDetailedInfoUseCase: GetWeatherDetailedInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getWeatherDetailedInfoUseCase = GetWeatherDetailedInfoUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Test
    fun `When GetWeatherDetailedInfoUseCase is invoked, we expect to receive WeatherDetailedInfo`() {
        // arrange
        val requestCityId: Int = 1234
        val expectedResult: WeatherDetailedInfo = mockk {
            every { cityName } returns "Kazan"
            every { temperature } returns 25.5
            every { feelsLike } returns 26.0
            every { humidity } returns 123
            every { pressure } returns 321
            every { windDegree } returns 12
            every { windSpeed } returns 1.2
            every { sunriseTime } returns 12_345_678
            every { sunsetTime } returns 87_654_321
            every { weatherDescription } returns "Sunny"
        }

        coEvery {
            weatherRepository.getWeather(cityId = requestCityId)
        } returns expectedResult

        // act
        runTest {
            val result = getWeatherDetailedInfoUseCase.invoke(cityId = requestCityId)

            // assert
            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun `When GetWeatherDetailedInfoUseCase is invoked and no Internet-connection, we expect UnknownHostException being threw`() {
        // arrange
        val requestCityId: Int = 1234

        coEvery {
            weatherRepository.getWeather(cityId = requestCityId)
        } throws UnknownHostException()

        // act
        runTest {
            // assert
            assertFailsWith<UnknownHostException> {
                getWeatherDetailedInfoUseCase.invoke(cityId = requestCityId)
            }
        }
    }
}
