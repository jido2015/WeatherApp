package com.test.weatherapp.domain.model

/**Sealed class for Handling Api Response*/
sealed class WeatherUiState<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : WeatherUiState<T>(data)
    class Error<T>(errorText: String?, data: T? = null) : WeatherUiState<T>(data, errorText)
    class Loading<Boolean> : WeatherUiState<Boolean>()
    class Empty<Nothing> : WeatherUiState<Nothing>()

}