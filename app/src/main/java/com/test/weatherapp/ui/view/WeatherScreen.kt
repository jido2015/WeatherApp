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

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    var city by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.observeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Displaying weather result (loading, success, or error) above the TextField
        when (uiState) {
            is WeatherUiState.Success -> {
                val weather = (uiState as WeatherUiState.Success).data
                Text(
                    text = "Temperature: ${weather.main.temp}°C",
                    modifier = Modifier
                        .padding(16.dp)
                        .semantics {
                            contentDescription = "Current temperature is ${weather.main.temp} degrees Celsius"
                        }
                )
                Text(
                    text = "Description: ${weather.weather[0].description}",
                    modifier = Modifier.padding(8.dp).semantics {
                        contentDescription = "Weather description is ${weather.weather[0].description}"
                    }
                )
                AsyncImage(
                    model = "https://openweathermap.org/img/w/${weather.weather[0].icon}.png",
                    modifier = Modifier.size(48.dp),
                    contentDescription = "Weather icon showing ${weather.weather[0].description}",
                )
            }

            is WeatherUiState.Error -> {
                val errorMessage = (uiState as WeatherUiState.Error).message
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .semantics {
                            contentDescription = "Error message: $errorMessage"
                        }
                )
            }

            else -> {
                Spacer(modifier = Modifier.height(16.dp)) // Space for the UI state area
            }
        }

        // Input for the city name
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter City") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("City name") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button with loader inside it
        Button(
            onClick = { viewModel.getWeather(city) },
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
