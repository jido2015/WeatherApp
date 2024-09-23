package com.test.weatherapp.domain.model

// Data classes for parsing weather data
data class WeatherResponse(
    val weather: List<Weather>, // List of weather conditions
    val main: Main // Main weather data (temperature, etc.)
)



data class Weather(val description: String, val icon: String) // Weather description and icon
data class Main(val temp: Double) // Main data containing temperature
