import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.data.model.WeatherUiState
import com.test.weatherapp.data.model.WeatherUiState.*
import com.test.weatherapp.domain.usecase.FetchWeatherUseCase
import com.test.weatherapp.domain.model.Result
import kotlinx.coroutines.launch

class WeatherViewModel(private val fetchWeatherUseCase: FetchWeatherUseCase) : ViewModel() {
    private val _uiState = MutableLiveData<WeatherUiState>()
    val uiState: LiveData<WeatherUiState> = _uiState

    fun getWeather(city: String) {
        viewModelScope.launch {
            _uiState.value = Loading
            val result = fetchWeatherUseCase(city)

            when (result) {
                is Result.Success -> {
                    _uiState.value = Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = Error(result.message)
                }

                Result.Loading -> {
                    Loading
                }
            }
        }
    }
}
