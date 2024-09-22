package com.test.weatherapp.data.api

import com.test.weatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Define an interface for the weather API service
interface WeatherApiService {
    // Fetch weather data for a specified city using a GET request
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String, // City name as query parameter
        @Query("appid") apiKey: String // API key for authentication
    ): Response<WeatherResponse>
}