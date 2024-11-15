package com.test.weatherapp.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.domain.model.WeatherUiState
import com.test.weatherapp.domain.model.WeatherUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val fetchWeatherUseCase: FetchWeatherUseCase,
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val appContext: Context // Inject Application context

) : ViewModel() {


    private val _uiState = MutableStateFlow<WeatherUiState<WeatherResponse>>(Loading())
    val uiState = _uiState

    // Load the last searched city when the ViewModel is created
    init {
        val lastSearchedCity = loadLastSearchedCity()
        if (lastSearchedCity != null) {
            getWeather(lastSearchedCity)
        }
    }

    // Function to get weather data by city name
    fun getWeather(city: String) {
        viewModelScope.launch {
            setLoadingState() // Set loading state before the API call
            val result = fetchWeatherUseCase(city) // Fetch weather data by city

            // Handle result
            handleResult(result)

            // Save the last searched city
            saveLastSearchedCity(city)
        }
    }

    // New function to get weather data by latitude and longitude
    fun getWeatherByLocation(location: Location) {
        viewModelScope.launch {
            setLoadingState() // Set loading state before the API call
            val result = fetchWeatherUseCase(appContext, location) // Fetch weather data by location

            when(result){
                is Result.Success -> {
                    getWeather(result.data) // Update getWeather with success data
                }
                is Result.Error -> {
                    _uiState.value = Error(result.message) // Update UI state with error message
                }
                Result.Loading -> {
                    // Loading state is already handled in setLoadingState
                }
            }

        }
    }

    // Set loading state
    private fun setLoadingState() {
        _uiState.value = Loading()
    }

    // Handle the result and update UI state
    private fun handleResult(result: Result<WeatherResponse>) {
        when (result) {
            is Result.Success -> {
                _uiState.value = Success(result.data) // Update UI state with success data
            }
            is Result.Error -> {
                _uiState.value = Error(result.message) // Update UI state with error message
            }
            Result.Loading -> {
                // Loading state is already handled in setLoadingState
            }
        }
    }

    // Save the last searched city to SharedPreferences
    private fun saveLastSearchedCity(city: String) {
        sharedPreferences.edit().putString("last_searched_city", city).apply()
    }

    // Load the last searched city from SharedPreferences
    fun loadLastSearchedCity(): String? {
        return sharedPreferences.getString("last_searched_city", null)
    }

}
