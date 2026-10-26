package wpics.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import wpics.weather.model.weather.UnitSystem
import wpics.weather.ui.theme.*

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UnitSelector(
    selectedUnit: UnitSystem,
    onUnitSelected: (UnitSystem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UnitSystem.values().forEach { unit ->
            Button(
                onClick = {
                    onUnitSelected(unit)
                },
                enabled = selectedUnit != unit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedUnit == unit) {
                        Color(0xFFFFC107)
                    } else {
                        Color.White.copy(alpha = 0.8f)
                    },
                    contentColor = Color(0xFF172033),
                    disabledContainerColor = Color(0xFFFFC107),
                    disabledContentColor = Color(0xFF172033)
                )
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
}

@Composable
fun WeatherLoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Loading weather...")
    }
}

@Composable
fun WeatherErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun WeatherUnavailableContent(
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Weather data is not available yet.")

        Spacer(Modifier.height(12.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun CurrentWeatherSummary(
    cityName: String,
    icon: String?,
    description: String?,
    temperature: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cityName,
            style = MaterialTheme.typography.headlineMedium
        )

        icon?.let {
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${it}@2x.png",
                contentDescription = description ?: "Current weather",
                modifier = Modifier.size(100.dp)
            )
        }

        Text(
            text = temperature,
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            text = description
                ?.replaceFirstChar { it.uppercase() }
                ?: "Conditions unavailable",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun CurrentConditionsCard(
    humidity: String,
    precipitation: String,
    pressure: String,
    sunrise: String,
    sunset: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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

            ConditionRow("Humidity", humidity)
            ConditionRow("Precipitation (last hour)", precipitation)
            ConditionRow("Pressure", pressure)
            ConditionRow("Sunrise", sunrise)
            ConditionRow("Sunset", sunset)
        }
    }
}

@Composable
private fun ConditionRow(
    label: String,
    value: String
) {
    Text(
        text = "$label: $value",
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun ForecastHeading() {
    Text(
        text = "5-day forecast",
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun ForecastUnavailableContent() {
    Text("Forecast data is unavailable.")
}

@Composable
fun ForecastDayCard(
    date: String,
    description: String?,
    icon: String?,
    highTemperature: String,
    lowTemperature: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description ?: "No description",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            icon?.let {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${it}.png",
                    contentDescription = description ?: "Forecast weather",
                    modifier = Modifier.size(56.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "H: $highTemperature",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "L: $lowTemperature",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

fun formatWeatherTime(timestampSeconds: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(Date(timestampSeconds * 1000L))
}

fun formatWeatherDate(date: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        formatter.format(parser.parse(date)!!)
    } catch (_: Exception) {
        date
    }
}

fun getWeatherBackground(icon: String?): Color {
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

fun getWeatherTextColor(background: Color): Color {
    return if (background.luminance() > 0.4f) {
        WeatherDayText
    } else {
        WeatherNightText
    }
}