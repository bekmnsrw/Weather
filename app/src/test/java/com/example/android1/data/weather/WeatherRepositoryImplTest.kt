package com.example.android1.data.weather

import com.example.android1.data.weather.datasource.local.WeatherMainInfoCache
import com.example.android1.data.weather.datasource.remote.WeatherApi
import com.example.android1.data.weather.datasource.remote.response.MultipleCity
import com.example.android1.data.weather.datasource.remote.response.MultipleWeatherResponse
import com.example.android1.data.weather.datasource.remote.response.WeatherResponse
import com.example.android1.data.weather.mapper.calculateColor
import com.example.android1.data.weather.mapper.toWeatherMainInfoList
import com.example.android1.domain.weather.WeatherDetailedInfo
import com.example.android1.domain.weather.WeatherMainInfo
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    @MockK
    lateinit var weatherApi: WeatherApi

    @MockK
    lateinit var weatherMainInfoCache: WeatherMainInfoCache

    private lateinit var weatherRepositoryImpl: WeatherRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        weatherRepositoryImpl = WeatherRepositoryImpl(
            weatherApi = weatherApi,
            weatherMainInfoCache = weatherMainInfoCache
        )
    }

    @Test
    fun `When call getWeather, expect WeatherDetailedInfo`() = runTest {
        // arrange
        val requestCityId: Int = 1234

        val expectedResult = WeatherDetailedInfo(
            cityName = "Kazan",
            temperature = 25.5,
            feelsLike = 26.0,
            humidity = 12,
            pressure = 12,
            windSpeed = 1.2,
            windDegree = 12,
            sunriseTime = 12_345_678,
            sunsetTime = 87_654_321,
            weatherDescription = "Sunny",
            weatherIcon = "test"
        )

        val expectedData = mockk<WeatherResponse> {
            every { name } returns "Kazan"
            every { main } returns mockk {
                every { temp } returns 25.5
                every { feelsLike } returns 26.0
                every { humidity } returns 12
                every { pressure } returns 12
            }
            every { sys } returns mockk {
                every { sunrise } returns 12_345_678
                every { sunset } returns 87_654_321
            }
            every { weather } returns listOf(
                mockk {
                    every { description } returns "Sunny"
                    every { icon } returns "test"
                }
            )
            every { wind } returns mockk {
                every { deg } returns 12
                every { speed } returns 1.2
            }
        }

        coEvery {
            weatherApi.getWeather(requestCityId)
        } returns expectedData

        // act
        val result = weatherRepositoryImpl.getWeather(requestCityId)

        // assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `When call getWeather and no Internet-connection, expect UnknownHostException`() = runTest {
        // arrange
        val requestCityId: Int = 1234

        coEvery {
            weatherApi.getWeather(requestCityId)
        } throws UnknownHostException()

        // assert
        assertFailsWith<UnknownHostException> {
            // act
            weatherRepositoryImpl.getWeather(requestCityId)
        }
    }

    @Test
    fun `When call getCityId, expect city's id`() = runTest {
        // arrange
        val requestCityName: String = "Kazan"
        val expectedResult: Int = 1234

        coEvery {
            weatherApi.getCityId(cityName = requestCityName).id
        } returns expectedResult

        // act
        val result = weatherRepositoryImpl.getCityId(cityName = requestCityName)

        // assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `When call getCityId and no Internet-connection, expect UnknownHostException`() = runTest {
        // arrange
        val requestCityName: String = "Kazan"

        coEvery {
            weatherApi.getCityId(cityName = requestCityName).id
        } throws UnknownHostException()

        // assert
        assertFailsWith<UnknownHostException> {
            // act
            weatherRepositoryImpl.getCityId(cityName = requestCityName)
        }
    }

    @Test
    fun `When call getWeatherInNearbyCities from local datasource, expect list of WeatherMainInfo from WeatherMainInfoCache`() =
        runTest {
            // arrange
            val requestQuery: Map<String, String> = mapOf(
                "lat" to "53.4",
                "lon" to "54.3",
                "cnt" to "3"
            )

            val requestIsLocal: Boolean = true

            val expectedResult: List<WeatherMainInfo> = mutableListOf(
                WeatherMainInfo(
                    cityId = 1,
                    weatherIcon = "test1",
                    cityName = "Kazan",
                    temperature = 25.5,
                    temperatureTextViewColor = 1,
                ),
                WeatherMainInfo(
                    cityId = 2,
                    weatherIcon = "test2",
                    cityName = "Kazan",
                    temperature = 26.0,
                    temperatureTextViewColor = 2,
                )
            )

            every { weatherMainInfoCache.cache } returns mutableListOf(
                WeatherMainInfo(
                    cityId = 1,
                    weatherIcon = "test1",
                    cityName = "Kazan",
                    temperature = 25.5,
                    temperatureTextViewColor = 1,
                ),
                WeatherMainInfo(
                    cityId = 2,
                    weatherIcon = "test2",
                    cityName = "Kazan",
                    temperature = 26.0,
                    temperatureTextViewColor = 2,
                )
            )

            // act
            val result = weatherRepositoryImpl.getWeatherInNearbyCities(
                query = requestQuery,
                isLocal = requestIsLocal
            )

            // assert
            assertEquals(expectedResult, result)
        }

    @Test
    fun `When call getWeatherInNearbyCities from local datasource and no Internet-connection, expect UnknownHostException`() =
        runTest {
            // arrange
            val requestQuery: Map<String, String> = mapOf(
                "lat" to "53.4",
                "lon" to "54.3",
                "cnt" to "3"
            )

            val requestIsLocal: Boolean = false

            coEvery {
                weatherApi.getWeatherInNearbyCities(map = requestQuery)
            } throws UnknownHostException()

            // assert
            assertFailsWith<UnknownHostException> {
                // act
                weatherRepositoryImpl.getWeatherInNearbyCities(
                    query = requestQuery,
                    isLocal = requestIsLocal
                )
            }
        }

//    @Test
//    fun `When call getWeatherInNearbyCities from remote datasource, expect list of WeatherMainInfo`() =
//        runTest {
//            // arrange
//            val requestQuery: Map<String, String> = mapOf(
//                "lat" to "53.4",
//                "lon" to "54.3",
//                "cnt" to "3"
//            )
//
//            val requestIsLocal: Boolean = false
//
//            val expectedResult: List<WeatherMainInfo> = arrayListOf(
//                WeatherMainInfo(
//                    cityId = 1,
//                    weatherIcon = "test1",
//                    cityName = "Kazan",
//                    temperature = 25.5,
//                    temperatureTextViewColor = calculateColor(25.5),
//                ),
//                WeatherMainInfo(
//                    cityId = 2,
//                    weatherIcon = "test2",
//                    cityName = "Kazan",
//                    temperature = 26.0,
//                    temperatureTextViewColor = calculateColor(26.0),
//                )
//            )
//
//            val expectedData: MultipleWeatherResponse = mockk {
//                every { list } returns arrayListOf(
//                    mockk {
//                        every { id } returns 1
//                        every { name } returns "Kazan"
//                        every { weather } returns arrayListOf(
//                            mockk {
//                                every { icon } returns "test1"
//                            }
//                        )
//                        every { main } returns mockk {
//                            every { temp } returns 25.5
//                        }
//                    },
//                    mockk {
//                        every { id } returns 2
//                        every { name } returns "Moscow"
//                        every { weather } returns arrayListOf(
//                            mockk {
//                                every { icon } returns "test2"
//                            }
//                        )
//                        every { main } returns mockk {
//                            every { temp } returns 26.0
//                        }
//                    }
//                )
//            }
//
//            coEvery {
//                weatherApi.getWeatherInNearbyCities(map = requestQuery)
//            } returns expectedData
//
//            // act
//            val result = weatherRepositoryImpl.getWeatherInNearbyCities(
//                query = requestQuery,
//                isLocal = requestIsLocal
//            )
//
//            // assert
//            assertEquals(expectedResult, result)
//        }
}
