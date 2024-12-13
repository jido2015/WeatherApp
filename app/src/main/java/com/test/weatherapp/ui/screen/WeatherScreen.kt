package com.test.weatherapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.weatherapp.domain.model.Result
import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.ui.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    var city by remember { mutableStateOf("") } // State for the city input
    val uiState by viewModel.uiState.collectAsState() // Observe the UI state

    // Load the last searched city into the TextField
    LaunchedEffect(uiState) {
        if (uiState is Result.Success) {
            city = viewModel.loadLastSearchedCity() ?: "" // Load the last searched city
        }
    }

    val scrollState = rememberScrollState() // Create a scroll state

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // Make the column scrollable
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            is Result.Success -> {
                val weather = (uiState as Result.Success<WeatherResponse>).data // Get weather data
                Text(
                    text = "Temperature: ${weather.main.temp}°C", // Display temperature
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Description: ${weather.weather[0].description}",
                    modifier = Modifier.padding(8.dp)
                )
                if (weather.weather.isNotEmpty()) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/w/${weather.weather[0].icon}.png",
                        modifier = Modifier.size(48.dp),
                        contentDescription = "Weather icon showing ${weather.weather[0].description}",
                    )
                }
            }

            is Result.Error -> {
                Text(
                    text = "Error: ${(uiState as Result.Error).message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

        }

        TextField(
            value = city,
            onValueChange = { city = it }, // Update city state on input change
            label = { Text("Enter City") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("City name") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.getWeather(city) }, // Call ViewModel method on click
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is Result.Loading // Disable button while loading
        ) {
            Text(text = "Get Weather")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun WeatherScreenPreview() {
    WeatherScreen() // Pass mock uiState directly
}

