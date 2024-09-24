package com.test.weatherapp

import android.content.Context
import android.location.Location
import com.test.weatherapp.data.repository.WeatherRepository
import com.test.weatherapp.domain.model.Main
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase
import junit.framework.TestCase.assertEquals
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.domain.model.Weather
import com.test.weatherapp.domain.model.WeatherResponse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After


@ExperimentalCoroutinesApi
class FetchWeatherUseCaseTest {

    private lateinit var fetchWeatherUseCase: FetchWeatherUseCase
    private lateinit var repository: WeatherRepository
    private lateinit var context: Context
    private lateinit var location: Location

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mock()
        fetchWeatherUseCase = FetchWeatherUseCase(repository)
        context = mock()

        location = mock(Location::class.java).apply {
            `when`(latitude).thenReturn(37.7749)
            `when`(longitude).thenReturn(-122.4194)
        }
    }


    @Test
    fun `fetchWeather returns data from repository`()  {
        runTest {
            // Given
            val weatherResponse = WeatherResponse(
                listOf(Weather("Clear", "01d")),
                Main(25.0)
            )
            `when`(repository.fetchWeather("New York")).thenReturn(Result.Success(weatherResponse))

            // When
            val result = fetchWeatherUseCase("New York")

            // Then
            assertEquals(weatherResponse, (result as Result.Success).data)
        }
    }

    @Test
    fun `fetchWeather returns error for unknown city`() {
        runTest {
            // Given
            `when`(repository.fetchWeather("Unknown City")).thenReturn(Result.Error("No data available"))

            // When
            val result = fetchWeatherUseCase("Unknown City")

            // Then
            assertTrue(result is Result.Error)
            assertEquals("No data available", (result as Result.Error).message)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher to the original
    }

}
