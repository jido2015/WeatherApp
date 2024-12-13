package com.test.weatherapp

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.test.weatherapp.domain.model.Main
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.domain.model.Weather
import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.domain.model.WeatherUiState
import com.test.weatherapp.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fetchWeatherUseCase: FetchWeatherUseCase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fetchWeatherUseCase = mock()
        sharedPreferences = mock()
        editor = mock()
        context = mock()

        // Mock SharedPreferences methods
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        `when`(editor.apply()).then { }

        weatherViewModel = WeatherViewModel(fetchWeatherUseCase, sharedPreferences, context)
    }

    @Test
    fun `getWeather should update UI state when use case returns data`() = runTest {
        // Given
        val weatherResponse = WeatherResponse(
            listOf(Weather("Clear", "01d")),
            Main(25.0)
        )
        `when`(fetchWeatherUseCase("New York")).thenReturn(Result.Success(weatherResponse))

        // Create a list to collect StateFlow emissions
        val collectedStates = mutableListOf<Result<WeatherResponse>>()

        // Collect emissions from the StateFlow in a coroutine
        val job = launch {
            weatherViewModel.uiState.collect {
                collectedStates.add(it)
            }
        }

        // When
        weatherViewModel.getWeather("New York")

        // Wait for emissions to propagate
        advanceUntilIdle()

        // Then
        assert(collectedStates[0] is Result.Loading) // Verify loading state
        assert(collectedStates[1] is Result.Success && (collectedStates[1] as Result.Success).data == weatherResponse) // Verify success state

        // Cleanup
        job.cancel()
    }

    @Test
    fun `getWeather should handle error response from use case`() = runTest {
        // Given
        `when`(fetchWeatherUseCase("Unknown City")).thenReturn(Result.Error("City not found"))

        // Create a list to collect StateFlow emissions
        val collectedStates = mutableListOf<Result<WeatherResponse>>()

        // Collect emissions from the StateFlow in a coroutine
        val job = launch {
            weatherViewModel.uiState.collect {
                collectedStates.add(it)
            }
        }

        // When
        weatherViewModel.getWeather("Unknown City")

        // Wait for emissions to propagate
        advanceUntilIdle()

        // Then
        assert(collectedStates[0] is Result.Loading) // Verify loading state
        assert(collectedStates[1] is Result.Error && (collectedStates[1] as Result.Error).message == "City not found") // Verify error state

        // Cleanup
        job.cancel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher to the original
    }
}
