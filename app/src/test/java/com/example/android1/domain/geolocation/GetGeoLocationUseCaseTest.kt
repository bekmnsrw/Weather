package com.example.android1.domain.geolocation

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
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class GetGeoLocationUseCaseTest {

    @MockK
    lateinit var geoLocationRepository: GeoLocationRepository

    private lateinit var getGeoLocationUseCase: GetGeoLocationUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getGeoLocationUseCase = GetGeoLocationUseCase(
            geoLocationRepository = geoLocationRepository
        )
    }

    @Test
    fun `When GetGeoLocationUseCase is invoked and permissions are granted we expect to receive GeoLocation`() {
        // arrange
        val requestArePermissionsGranted: Boolean = true
        val expectedResult: GeoLocation = mockk {
            every { longitude } returns 12.3
            every { latitude } returns 25.5
        }

        coEvery {
            geoLocationRepository.getLocation(
                arePermissionsGranted = requestArePermissionsGranted
            )
        } returns expectedResult

        // act
        runTest {
            val result = getGeoLocationUseCase.invoke(
                arePermissionsGranted = requestArePermissionsGranted
            )

            // assert
            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun `When GetGeoLocationUseCase is invoked and permissions aren't granted, we expect to receive GeoLocation with null values`() {
        // arrange
        val requestArePermissionsGranted: Boolean = false
        val expectedResult: GeoLocation = mockk {
            every { longitude } returns null
            every { latitude } returns null
        }

        coEvery {
            geoLocationRepository.getLocation(
                arePermissionsGranted = requestArePermissionsGranted
            )
        } returns expectedResult

        // act
        runTest {
            val result = getGeoLocationUseCase.invoke(
                arePermissionsGranted = requestArePermissionsGranted
            )

            // assert
            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun `When GetGeoLocationUseCase is invoked and GPS doesn't turned on we expect exception being threw`() {
        // arrange
        val requestArePermissionsGranted: Boolean = true

        coEvery {
            geoLocationRepository.getLocation(
                arePermissionsGranted = requestArePermissionsGranted
            )
        } throws Throwable()

        // act
        runTest {
            // assert
            assertFailsWith<Throwable> {
                getGeoLocationUseCase.invoke(
                    arePermissionsGranted = requestArePermissionsGranted
                )
            }
        }
    }
}
