package com.test.weatherapp.data.model

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main
)

data class Weather(val description: String, val icon: String)
data class Main(val temp: Double)
