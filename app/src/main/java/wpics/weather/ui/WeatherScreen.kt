
package wpics.weather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.UnitSystem
import wpics.weather.viewmodel.WeatherViewModel
import wpics.weather.viewmodel.groupForecastByDay
import wpics.weather.ui.theme.*
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

    // Fetch weather when this screen receives a coordinate.
    LaunchedEffect(coordinate) {
        coordinate?.let { weatherViewModel.fetchData(it) }
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UnitSystem.values().forEach { unit ->
                    Button(
                        onClick = {
                            selectedUnit = unit
                            weatherViewModel.updateUnitSystem(unit)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedUnit == unit) {
                                Color(0xFFFFC107) // Bright amber
                            } else {
                                Color.White.copy(alpha = 0.8f)
                            },
                            contentColor = Color(0xFF172033),
                            disabledContainerColor = Color(0xFFFFC107),
                            disabledContentColor = Color(0xFF172033)
                        ),
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                            Text(
                                text = state.errorMessage!!,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { weatherViewModel.retry() }
                            ) {
                                Text("Retry")
                            }
                        }
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

                        // Current weather details
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Current conditions",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    HorizontalDivider()

                                    Text(
                                        text = "Humidity: " +
                                                (current?.humidity?.let { "$it%" }
                                                    ?: "N/A"),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "Precipitation (last hour): " +
                                                (weather.rain?.oneHour?.let {
                                                    "$it $precipitationUnit"
                                                } ?: "N/A"),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "Pressure: " +
                                                (current?.pressure?.let { "$it hPa" }
                                                    ?: "N/A"),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "Sunrise: " +
                                                (weather.sys?.sunrise?.let {
                                                    formatTime(it)
                                                } ?: "N/A"),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "Sunset: " +
                                                (weather.sys?.sunset?.let {
                                                    formatTime(it)
                                                } ?: "N/A"),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Forecast heading
                        item {
                            Text(
                                text = "5-day forecast",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // Handle a missing or empty forecast
                        if (forecastDays.isEmpty()) {
                            item {
                                Text("Forecast data is unavailable.")
                            }
                        } else {
                            items(forecastDays) { day ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment =
                                            Alignment.CenterVertically,
                                        horizontalArrangement =
                                            Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = formatDate(day.date),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = day.description
                                                    ?: "No description",
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        day.icon?.let { icon ->
                                            AsyncImage(
                                                model = "https://openweathermap.org/img/wn/${icon}.png",
                                                contentDescription = day.description
                                                    ?: "Forecast weather",
                                                modifier = Modifier.size(56.dp)
                                            )
                                        }

                                        Column(
                                            horizontalAlignment =
                                                Alignment.End
                                        ) {
                                            Text(
                                                text = "H: ${day.maxTemp.roundToInt()}$temperatureUnit",
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = "L: ${day.minTemp.roundToInt()}$temperatureUnit",
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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

private fun getWeatherBackground(icon: String?): Color {
    val isDay = icon?.endsWith("d") == true
    val conditionCode = icon?.take(2)

    return when (conditionCode) {
        "01" ->
            if (isDay) ClearDayBackground else ClearNightBackground

        "02", "03", "04" ->
            if (isDay) CloudsDayBackground else CloudsNightBackground

        "09", "10" ->
            if (isDay) RainDayBackground else RainNightBackground

        "11" ->
            if (isDay) StormDayBackground else StormNightBackground

        "13" ->
            if (isDay) SnowDayBackground else SnowNightBackground

        "50" ->
            if (isDay) MistDayBackground else MistNightBackground

        else ->
            if (isDay) LightBackground else DarkBackground
    }
}

private fun getWeatherTextColor(background: Color): Color {
    return if (background.luminance() > 0.4f) {
        WeatherDayText
    } else {
        WeatherNightText
    }
}
