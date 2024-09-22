package com.test.weatherapp.domain.usecase

import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.data.model.WeatherResponse
import com.test.weatherapp.data.repository.WeatherRepository

// Use case for fetching weather data
class FetchWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): Result<WeatherResponse> {
        return repository.fetchWeather(city) // Invoke the repository method
    }
}

