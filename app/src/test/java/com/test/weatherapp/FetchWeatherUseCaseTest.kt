package com.test.weatherapp

import com.test.weatherapp.data.model.Main
import com.test.weatherapp.data.model.Weather
import com.test.weatherapp.data.model.WeatherResponse
import com.test.weatherapp.data.repository.WeatherRepository
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class FetchWeatherUseCaseTest {

    private lateinit var fetchWeatherUseCase: FetchWeatherUseCase
    private lateinit var repository: WeatherRepository

    @Before
    fun setup() {
        repository = mock()
        fetchWeatherUseCase = FetchWeatherUseCase(repository)
    }

    @Test
    fun `fetchWeather returns data from repository`()  {

        runTest{
            // Given
            val weatherResponse = WeatherResponse(
                listOf(Weather("Clear", "01d")),
                Main(25.0)
            )
            `when`(repository.fetchWeather("New York")).thenReturn(weatherResponse)

            // When
            val result = fetchWeatherUseCase("New York")

            // Then
            assertEquals(weatherResponse, result)
        }
    }

    @Test
    fun `fetchWeather returns null for unknown city`() {
        runTest {
            // Given
            `when`(repository.fetchWeather("Unknown City")).thenReturn(null)

            // When
            val result = fetchWeatherUseCase("Unknown City")

            // Then
            assertNull(result)
        }
    }
}
