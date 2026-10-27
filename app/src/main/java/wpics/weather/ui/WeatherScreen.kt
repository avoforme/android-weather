package wpics.weather.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.UnitSystem
import wpics.weather.ui.components.CurrentConditionsCard
import wpics.weather.ui.components.CurrentWeatherSummary
import wpics.weather.ui.components.ForecastDayCard
import wpics.weather.ui.components.ForecastHeading
import wpics.weather.ui.components.ForecastUnavailableContent
import wpics.weather.ui.components.UnitSelector
import wpics.weather.ui.components.WeatherErrorContent
import wpics.weather.ui.components.WeatherLoadingContent
import wpics.weather.ui.components.WeatherUnavailableContent
import wpics.weather.ui.components.formatWeatherDate
import wpics.weather.ui.components.formatWeatherTime
import wpics.weather.ui.components.getWeatherBackground
import wpics.weather.ui.components.getWeatherTextColor
import wpics.weather.viewmodel.WeatherViewModel
import wpics.weather.viewmodel.groupForecastByDay
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    coordinate: Coordinate?
) {
    val state by weatherViewModel.uiState.collectAsState()

    var selectedUnit by remember {
        mutableStateOf(UnitSystem.METRIC)
    }

    // Fetch weather when this screen receives a coordinate.
    LaunchedEffect(coordinate) {
        coordinate?.let {
            weatherViewModel.fetchData(it)
        }
    }

    val temperatureUnit = when (selectedUnit) {
        UnitSystem.METRIC -> "°C"
        UnitSystem.IMPERIAL -> "°F"
        UnitSystem.STANDARD -> "K"
    }

    val precipitationUnit = when (selectedUnit) {
        UnitSystem.METRIC -> "mm"
        UnitSystem.IMPERIAL -> "mm"
        UnitSystem.STANDARD -> "mm"
    }

    val forecastDays = remember(state.forecast) {
        state.forecast?.let {
            groupForecastByDay(it)
        }.orEmpty()
    }

    // Choose the background using the current weather icon.
    val currentIcon = state.currentWeather
        ?.weather
        ?.firstOrNull()
        ?.icon

    val weatherBackground = getWeatherBackground(currentIcon)
    val weatherTextColor = getWeatherTextColor(weatherBackground)

    CompositionLocalProvider(
        LocalContentColor provides weatherTextColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(weatherBackground)
        ) {
            // Unit selection buttons
            UnitSelector(
                selectedUnit = selectedUnit,
                onUnitSelected = { unit ->
                    selectedUnit = unit
                    weatherViewModel.updateUnitSystem(unit)
                }
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        WeatherLoadingContent()
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        WeatherErrorContent(
                            message = state.errorMessage!!,
                            onRetry = {
                                weatherViewModel.retry()
                            }
                        )
                    }
                }

                state.currentWeather != null -> {
                    val weather = state.currentWeather!!
                    val current = weather.main
                    val condition = weather.weather?.firstOrNull()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Current weather summary
                        item {
                            CurrentWeatherSummary(
                                cityName = weather.name ?: "Unknown city",
                                icon = condition?.icon,
                                description = condition?.description,
                                temperature = current?.temp?.let {
                                    "${it.roundToInt()} $temperatureUnit"
                                } ?: "Temperature unavailable"
                            )
                        }
                        Log.d("WeatherDebug", "Rain oneHour: ${weather.rain?.oneHour}")                        // Current weather details
                        item {
                            CurrentConditionsCard(
                                humidity = current?.humidity?.let {
                                    "$it%"
                                } ?: "N/A",
                                precipitation = weather.rain?.oneHour?.let {
                                    "$it $precipitationUnit"
                                } ?: "0.00 $precipitationUnit",
                                pressure = current?.pressure?.let {
                                    "$it hPa"
                                } ?: "N/A",
                                sunrise = weather.sys?.sunrise?.let {
                                    formatWeatherTime(it)
                                } ?: "N/A",
                                sunset = weather.sys?.sunset?.let {
                                    formatWeatherTime(it)
                                } ?: "N/A"
                            )
                        }

                        // Forecast heading
                        item {
                            ForecastHeading()
                        }

                        // Forecast list
                        if (forecastDays.isEmpty()) {
                            item {
                                ForecastUnavailableContent()
                            }
                        } else {
                            items(forecastDays) { day ->
                                ForecastDayCard(
                                    date = formatWeatherDate(day.date),
                                    description = day.description,
                                    icon = day.icon,
                                    highTemperature =
                                        "${day.maxTemp.roundToInt()}$temperatureUnit",
                                    lowTemperature =
                                        "${day.minTemp.roundToInt()}$temperatureUnit"
                                )
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        WeatherUnavailableContent(
                            onRetry = {
                                if (coordinate != null) {
                                    weatherViewModel.fetchData(coordinate)
                                } else {
                                    weatherViewModel.retry()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}