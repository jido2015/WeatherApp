package com.test.weatherapp.data.repository

import com.test.weatherapp.data.api.WeatherApiService
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.data.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    suspend fun fetchWeather(city: String): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeatherByCity(city, "11205995d6161a2bb6d94f004a154a5d")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("No data available")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception: ${e.message}")
        }
    }
}
