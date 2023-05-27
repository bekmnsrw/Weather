package com.example.android1.data.geolocation

import android.location.Location
import com.example.android1.domain.geolocation.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class GeoLocationRepositoryImplTest {

    @MockK
    lateinit var client: FusedLocationProviderClient

    private lateinit var geoLocationRepositoryImpl: GeoLocationRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        geoLocationRepositoryImpl = GeoLocationRepositoryImpl(client = client)
    }

    @Test
    fun `When call getLocation and permissions aren't granted, expect GeoLocation with null values`() = runTest {
        // arrange
        val arePermissionsGrantedRequest: Boolean = false

        val expectedResult: GeoLocation = GeoLocation(
            longitude = null,
            latitude = null
        )

        // act
        val result = geoLocationRepositoryImpl.getLocation(
            arePermissionsGranted = arePermissionsGrantedRequest
        )

        // assert
        assertEquals(expectedResult, result)
    }

//    @Test
//    fun `When call getLocation and permissions are granted, expect GeoLocation`() = runTest {
//        // arrange
//        val arePermissionsGrantedRequest: Boolean = true
//
//        val expectedResult: GeoLocation = mockk {
//            every { longitude } returns 12.2
//            every { latitude } returns 12.2
//        }
//
//        val expectedData: Task<Location> = mockk {
//            every { result.longitude } returns 12.2
//            every { result.latitude } returns 12.2
//        }
//
//        coEvery {
//            client.lastLocation
//        } returns expectedData
//
//        // act
//        val result = geoLocationRepositoryImpl.getLocation(
//            arePermissionsGranted = arePermissionsGrantedRequest
//        )
//
//        // assert
//        assertEquals(expectedResult, result)
//    }
}
