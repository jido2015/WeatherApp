package com.test.weatherapp.data.repository

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.test.weatherapp.data.api.WeatherApiService
import com.test.weatherapp.data.location.LocationService
import com.test.weatherapp.data.location.getCity
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.domain.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

// Repository for managing weather data
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApiService
) {
    // Fetch weather data and return a Result wrapper
    suspend fun fetchWeather(city: String): Result<WeatherResponse> {
        return try {
            val response = weatherApi.getWeatherByCity(city, "11205995d6161a2bb6d94f004a154a5d") // Make API call
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

    suspend fun getCityName(context: Context, location: Location): Result<String> {
        return try {
            // Create an instance of Geocoder
            val geocoder = Geocoder(context) // Ensure you have context

            // Directly call getCity()
            val city = geocoder.getCity(location.latitude, location.longitude)
           if (city != null) {
               Result.Success(city)
           } else {
               Result.Error("City not found")
           }
        } catch (e: Exception) {
            Result.Error("Exception: ${e.message}") // Handle exceptions during the API call
        }
    }
}
