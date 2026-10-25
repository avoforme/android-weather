package wpics.weather.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.UnitSystem
import wpics.weather.viewmodel.WeatherViewModel
import wpics.weather.viewmodel.groupForecastByDay

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    coordinate: Coordinate?
) {
    val state by weatherViewModel.uiState.collectAsState()
    var selectedUnit by remember { mutableStateOf(UnitSystem.METRIC) }

    val temperatureUnit = when (selectedUnit) {
        UnitSystem.METRIC -> "°C"
        UnitSystem.IMPERIAL -> "°F"
        UnitSystem.STANDARD -> "K"
    }

    val precipitationUnit = when (selectedUnit) {
        UnitSystem.METRIC -> "mm"
        UnitSystem.IMPERIAL -> "in"
        UnitSystem.STANDARD -> "mm"
    }

    val forecastDays = remember(state.forecast) {
        state.forecast?.let { groupForecastByDay(it) }.orEmpty()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Unit selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UnitSystem.entries.forEach { unit ->
                Button(
                    onClick = {
                        selectedUnit = unit
                        weatherViewModel.updateUnitSystem(unit)
                    },
                    enabled = selectedUnit != unit
                ) {
                    Text(
                        when (unit) {
                            UnitSystem.METRIC -> "Metric"
                            UnitSystem.IMPERIAL -> "Imperial"
                            UnitSystem.STANDARD -> "Standard"
                        }
                    )
                }
            }
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Loading weather...")
                    }
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(state.errorMessage!!)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { weatherViewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            state.currentWeather != null -> {
                val weather = state.currentWeather!!
                val current = weather.main
                val condition = weather.weather?.firstOrNull()
                val forecastAvailable = forecastDays.isNotEmpty()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = weather.name ?: "Unknown city",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            condition?.icon?.let { icon ->
                                AsyncImage(
                                    model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                                    contentDescription = condition.description
                                        ?: "Current weather",
                                    modifier = Modifier.size(100.dp)
                                )
                            }

                            Text(
                                text = current?.temp?.let {
                                    "${it.roundToInt()} $temperatureUnit"
                                } ?: "Temperature unavailable",
                                style = MaterialTheme.typography.displaySmall
                            )

                            Text(
                                text = condition?.description
                                    ?.replaceFirstChar { it.uppercase() }
                                    ?: "Conditions unavailable",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Current conditions",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                HorizontalDivider()

                                Text("Humidity: ${current?.humidity?.let { "$it%" } ?: "N/A"}")
                                Text(
                                    "Precipitation (last hour): " +
                                            (weather.rain?.oneHour?.let {
                                                "$it $precipitationUnit"
                                            } ?: "0 $precipitationUnit")
                                )
                                Text(
                                    "Pressure: ${current?.pressure?.let { "$it hPa" } ?: "N/A"}"
                                )
                                Text("Sunrise: ${weather.sys?.sunrise?.let { formatTime(it) } ?: "N/A"}")
                                Text("Sunset: ${weather.sys?.sunset?.let { formatTime(it) } ?: "N/A"}")
                            }
                        }
                    }

                    item {
                        Text(
                            "5-day forecast",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (!forecastAvailable) {
                        item {
                            Text("Forecast data is unavailable.")
                        }
                    } else {
                        items(forecastDays) { day ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            formatDate(day.date),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(day.description ?: "No description")
                                    }

                                    day.icon?.let { icon ->
                                        AsyncImage(
                                            model = "https://openweathermap.org/img/wn/${icon}.png",
                                            contentDescription = day.description,
                                            modifier = Modifier.size(56.dp)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("H: ${day.maxTemp.roundToInt()}$temperatureUnit")
                                        Text("L: ${day.minTemp.roundToInt()}$temperatureUnit")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Weather data is not available yet.")
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (coordinate != null) {
                                    weatherViewModel.fetchData(coordinate)
                                } else {
                                    weatherViewModel.retry()
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(timestampSeconds: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(Date(timestampSeconds * 1000L))
}

private fun formatDate(date: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        formatter.format(parser.parse(date)!!)
    } catch (_: Exception) {
        date
    }
}
