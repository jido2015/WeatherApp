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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
    private lateinit var observer: Observer<WeatherUiState>
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
        observer = mock()
    }

    @Test
    fun `getWeather should update UI state when use case returns data`() {
        runTest {
            // Given
            val weatherResponse = WeatherResponse(
                listOf(Weather("Clear", "01d")),
                Main(25.0)
            )
            `when`(fetchWeatherUseCase("New York")).thenReturn(Result.Success(weatherResponse))

            // Attach observer to LiveData
            weatherViewModel.uiState.observeForever(observer)

            // When
            weatherViewModel.getWeather("New York")

            // Then
            verify(observer).onChanged(WeatherUiState.Loading) // Verify loading state
            verify(observer).onChanged(WeatherUiState.Success(weatherResponse)) // Verify success state
            verifyNoMoreInteractions(observer)
        }
    }

    @Test
    fun `getWeather should handle error response from use case`() {
        runTest {
            // Given
            `when`(fetchWeatherUseCase("Unknown City")).thenReturn(Result.Error("City not found"))

            // Attach observer to LiveData
            weatherViewModel.uiState.observeForever(observer)

            // When
            weatherViewModel.getWeather("Unknown City")

            // Then
            verify(observer).onChanged(WeatherUiState.Loading) // Verify loading state
            verify(observer).onChanged(WeatherUiState.Error("City not found")) // Verify error state
            verifyNoMoreInteractions(observer)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher to the original
    }
}
