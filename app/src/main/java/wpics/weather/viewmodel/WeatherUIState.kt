package wpics.weather.viewmodel

import wpics.weather.model.weather.ForecastResponse
import wpics.weather.model.weather.WeatherResponse

data class WeatherUIState(
    val isLoading: Boolean = false,
    val currentWeather: WeatherResponse? = null,
    val forecast: ForecastResponse? = null,
    val errorMessage: String? = null
)