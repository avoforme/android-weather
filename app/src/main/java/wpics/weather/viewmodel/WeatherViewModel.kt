package wpics.weather.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.ForecastDay
import wpics.weather.model.weather.ForecastResponse
import wpics.weather.model.weather.UnitSystem
import wpics.weather.model.weather.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Locale

fun groupForecastByDay(
    forecast: ForecastResponse
): List<ForecastDay> {
    val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val grouped = forecast.list.orEmpty()
        .filter { it.dtTxt != null && it.main?.temp != null }
        .groupBy { it.dtTxt!!.substringBefore(" ") }
        .toSortedMap()

    return grouped.map { (date, entries) ->
        val temperatures = entries.mapNotNull { it.main?.temp }
        val midday = entries.minByOrNull { item ->
            val hour = item.dtTxt?.substringAfter(" ")?.take(2)
                ?.toIntOrNull() ?: 12
            kotlin.math.abs(hour - 12)
        }

        ForecastDay(
            date = date,
            minTemp = temperatures.minOrNull()!!,
            maxTemp = temperatures.maxOrNull()!!,
            icon = midday?.weather?.firstOrNull()?.icon,
            description = midday?.weather?.firstOrNull()?.description
        )
    }.take(5)
}


class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    companion object {
        private const val TAG = "WeatherViewModel"
    }

    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    private var selectedUnitSystem = UnitSystem.METRIC
    private var lastCoordinate: Coordinate? = null

    fun fetchData(coordinate: Coordinate) {
        lastCoordinate = coordinate

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val currentWeather = repository.getWeather(
                    coordinate,
                    selectedUnitSystem
                )

                val forecast = repository.getForecast(
                    coordinate,
                    selectedUnitSystem
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentWeather = currentWeather,
                    forecast = forecast,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch weather data", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage =
                        "Unable to load weather. Check your connection and try again."
                )
            }
        }
    }

    fun updateUnitSystem(unitSystem: UnitSystem) {
        if (selectedUnitSystem == unitSystem) return

        selectedUnitSystem = unitSystem

        lastCoordinate?.let { coordinate ->
            fetchData(coordinate)
        }
    }

    fun retry() {
        lastCoordinate?.let { fetchData(it) }
    }
}
