package com.test.weatherapp.data.repository

import com.test.weatherapp.data.api.WeatherApiService
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.data.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Repository for managing weather data
class WeatherRepository {
    // Lazy initialization of the API service
    private val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/") // Base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Converter for JSON to Kotlin objects
            .build()
            .create(WeatherApiService::class.java) // Create an instance of the API service
    }

    // Fetch weather data and return a Result wrapper
    suspend fun fetchWeather(city: String): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeatherByCity(city, "11205995d6161a2bb6d94f004a154a5d") // Make API call
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body) // Return successful result
                } else {
                    Result.Error("No data available") // Handle case where body is null
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}") // Handle API error response
            }
        } catch (e: Exception) {
            Result.Error("Exception: ${e.message}") // Handle exceptions during the API call
        }
    }
}