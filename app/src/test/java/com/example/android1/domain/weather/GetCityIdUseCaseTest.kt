package com.example.android1.domain.weather

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class GetCityIdUseCaseTest {

    @MockK
    lateinit var weatherRepository: WeatherRepository

    private lateinit var getCityIdUseCase: GetCityIdUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getCityIdUseCase = GetCityIdUseCase(weatherRepository = weatherRepository)
    }

    @Test
    fun `When GetCityIdUseCase is invoked, we expect to receive city's ID`() {
        // arrange
        val requestCityName: String = "Kazan"
        val expectedResult: Int = 1234

        coEvery {
            weatherRepository.getCityId(cityName = requestCityName)
        } returns expectedResult

        // act
        runTest {
            val result = getCityIdUseCase.invoke(cityName = requestCityName)

            // assert
            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun `When GetCityIdUseCase is invoked and no Internet-connection, we expect UnknownHostException being threw`() {
        // arrange
        val requestCityName: String = "Kazan"

        coEvery {
            weatherRepository.getCityId(cityName = requestCityName)
        } throws UnknownHostException()

        // act
        runTest {
            // assert
            assertFailsWith<UnknownHostException> {
                getCityIdUseCase.invoke(cityName = requestCityName)
            }
        }
    }
}
