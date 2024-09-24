package com.test.weatherapp.domain.usecase

import android.content.Context
import android.location.Location
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.data.repository.WeatherRepository
import com.test.weatherapp.domain.model.WeatherResponse
import javax.inject.Inject

// Use case for fetching weather data
class FetchWeatherUseCase @Inject constructor(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): Result<WeatherResponse> {
        return repository.fetchWeather(city) // Invoke the repository method
    }

    suspend operator fun invoke(context: Context, location: Location): Result<String> {
        return repository.getCityName(context,location) // Invoke the repository method
    }
}

