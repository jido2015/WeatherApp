import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.data.model.WeatherResponse
import com.test.weatherapp.data.model.WeatherUiState
import com.test.weatherapp.data.model.WeatherUiState.*
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase
import com.test.weatherapp.domain.model.Result
import kotlinx.coroutines.launch

class WeatherViewModel(private val fetchWeatherUseCase: FetchWeatherUseCase) : ViewModel() {
    private val _uiState = MutableLiveData<WeatherUiState>()
    val uiState: LiveData<WeatherUiState> = _uiState

    // Function to get weather data
    fun getWeather(city: String) {
        viewModelScope.launch {
            setLoadingState() // Set loading state before the API call
            val result = fetchWeatherUseCase(city) // Fetch weather data

            // Handle result
            handleResult(result)
        }
    }

    // Set loading state
    private fun setLoadingState() {
        _uiState.value = Loading
    }

    // Handle the result and update UI state
    private fun handleResult(result: Result<WeatherResponse>) {
        when (result) {
            is Result.Success -> {
                _uiState.value = Success(result.data) // Update UI state with success data
            }
            is Result.Error -> {
                _uiState.value = Error(result.message) // Update UI state with error message
            }
            Result.Loading -> {
                // Loading state is already handled in setLoadingState
            }
        }
    }
}

