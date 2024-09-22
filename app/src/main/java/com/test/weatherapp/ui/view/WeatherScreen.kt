package com.test.weatherapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.test.weatherapp.data.model.WeatherUiState

// Composable function for displaying the weather screen
@Composable
fun WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    var city by remember { mutableStateOf("") } // State for the city input
    val uiState by viewModel.uiState.observeAsState() // Observe UI state from ViewModel

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display weather result based on the current UI state
        when (uiState) {
            is WeatherUiState.Success -> {
                val weather = (uiState as WeatherUiState.Success).data // Get weather data
                Text(
                    text = "Temperature: ${weather.main.temp}°C", // Display temperature
                    modifier = Modifier
                        .padding(16.dp)
                        .semantics {
                            contentDescription = "Current temperature is ${weather.main.temp} degrees Celsius"
                        }
                )
                Text(
                    text = "Description: ${weather.weather[0].description}", // Display weather description
                    modifier = Modifier.padding(8.dp).semantics {
                        contentDescription = "Weather description is ${weather.weather[0].description}"
                    }
                )
                AsyncImage(
                    model = "https://openweathermap.org/img/w/${weather.weather[0].icon}.png", // Display weather icon
                    modifier = Modifier.size(48.dp),
                    contentDescription = "Weather icon showing ${weather.weather[0].description}",
                )
            }

            is WeatherUiState.Error -> {
                val errorMessage = (uiState as WeatherUiState.Error).message // Get error message
                Text(
                    text = "Error: $errorMessage", // Display error message
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .semantics {
                            contentDescription = "Error message: $errorMessage"
                        }
                )
            }

            else -> {
                Spacer(modifier = Modifier.height(16.dp)) // Space for UI state area
            }
        }

        // TextField for city name input
        TextField(
            value = city,
            onValueChange = { city = it }, // Update city state on input change
            label = { Text("Enter City") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("City name") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to fetch weather data
        Button(
            onClick = { viewModel.getWeather(city) }, // Call ViewModel method on click
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is WeatherUiState.Loading // Disable button while loading
        ) {
            if (uiState is WeatherUiState.Loading) {
                // Show loader inside the button
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary, // Set loader color
                    modifier = Modifier.size(24.dp), // Loader size
                    strokeWidth = 2.dp
                )
            } else {
                // Show text when not loading
                Text(
                    text = "Get Weather",
                    modifier = Modifier.semantics {
                        this.contentDescription = "Get weather information for the entered city"
                    }.padding(8.dp),
                )
            }
        }
    }
}