package com.test.weatherapp.data.model

// Sealed class for UI state representation
sealed class WeatherUiState {
    object Loading : WeatherUiState() // Loading state
    data class Success(val data: WeatherResponse) : WeatherUiState() // Success state with data
    data class Error(val message: String) : WeatherUiState() // Error state with message
}