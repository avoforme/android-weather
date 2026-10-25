package wpics.weather.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.WeatherRepository
import wpics.weather.model.weather.UnitSystem

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    // Example coordinates for London
    private val coordinate = Coordinate(51.5074, -0.1278)

    init {
        // Automatically fetch weather when the ViewModel is created
        loadWeather()
    }

    fun loadWeather() {
        // viewModelScope ensures this coroutine cancels automatically if the ViewModel is cleared
        viewModelScope.launch {
            try {
                val response = repository.getWeather(coordinate, UnitSystem.METRIC)
                Log.d("WeatherViewModel", "Success: ${response.name}")
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
            }
        }
    }
}