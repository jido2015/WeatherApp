package com.test.weatherapp.ui.viewmodel

import WeatherViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase

// Factory for creating WeatherViewModel instances
class WeatherViewModelFactory(private val fetchWeatherUseCase: FetchWeatherUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(fetchWeatherUseCase) as T // Return the ViewModel instance
        }
        throw IllegalArgumentException("Unknown ViewModel class") // Handle unknown ViewModel class
    }
}