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
class GetWeatherMainInfoUseCaseTest {

    @MockK
    lateinit var weatherRepository: WeatherRepository

    private lateinit var getWeatherMainInfoUseCase: GetWeatherMainInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getWeatherMainInfoUseCase = GetWeatherMainInfoUseCase(
            weatherRepository = weatherRepository
        )
    }

    @Test
    fun `When GetWeatherMainInfoUseCase is invoked, we expect to receive WeatherMainInfo`() {
        // arrange
        val requestQuery: Map<String, String> = mapOf(
            "lat" to "53.4",
            "lon" to "54.3",
            "cnt" to "3"
        )
        val requestIsLocal: Boolean = false
        val expectedResult: List<WeatherMainInfo> = arrayListOf<WeatherMainInfo>(
            mockk {
                every { cityId } returns 1
                every { cityName } returns "Kazan"
            },
            mockk {
                every { cityId } returns 2
                every { cityName } returns "Moscow"
            },
            mockk {
                every { cityId } returns 3
                every { cityName } returns "Izhevsk"
            }
        )

        coEvery {
            weatherRepository.getWeatherInNearbyCities(
                query = requestQuery,
                isLocal = requestIsLocal
            )
        } returns expectedResult

        // act
        runTest {
            val result = getWeatherMainInfoUseCase.invoke(
                query = requestQuery,
                isLocal = requestIsLocal
            )

            // assert
            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun `When GetWeatherMainInfoUseCase is invoked and no Internet-connection, we expect UnknownHostException being threw`() {
        // arrange
        val requestQuery: Map<String, String> = mapOf(
            "lat" to "53.4",
            "lon" to "54.3",
            "cnt" to "3"
        )
        val requestIsLocal: Boolean = false

        coEvery {
            weatherRepository.getWeatherInNearbyCities(
                query = requestQuery,
                isLocal = requestIsLocal
            )
        } throws UnknownHostException()

        // act
        runTest {
            // assert
            assertFailsWith<UnknownHostException> {
                getWeatherMainInfoUseCase(
                    query = requestQuery,
                    isLocal = requestIsLocal
                )
            }
        }
    }
}
