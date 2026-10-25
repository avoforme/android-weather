
// TODO: Add your name here as a header comment (required on every Kotlin file you write or edit).
package wpics.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.UnitSystem
import wpics.weather.model.weather.WeatherRepository
import wpics.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TEMPORARY TEST: Fetch weather for London.
        lifecycleScope.launch {
            try {
                val repository = WeatherRepository()
                val coordinate = Coordinate(
                    lat = 51.5074,
                    lon = -0.1278
                )

                val response = repository.getWeather(
                    coordinate,
                    UnitSystem.METRIC
                )

                Log.d(
                    "WeatherTest",
                    """
                    City: ${response.name}
                    Temperature: ${response.main?.temp} °C
                    Humidity: ${response.main?.humidity}%
                    Pressure: ${response.main?.pressure} hPa
                    Description: ${response.weather?.firstOrNull()?.description}
                    Wind speed: ${response.wind?.speed} m/s
                    """.trimIndent()
                )

                val forecastResponse = repository.getForecast(
                    coordinate,
                    UnitSystem.METRIC
                )

                Log.d(
                    "ForecastTest",
                    "Forecast city: ${forecastResponse.city?.name}"
                )

                val forecastItems = forecastResponse.list

                if (forecastItems.isNullOrEmpty()) {
                    Log.e("ForecastTest", "No forecast items were parsed")
                } else {
                    forecastItems.take(5).forEachIndexed { index, item ->
                        Log.d(
                            "ForecastTest",
                            """
                            Forecast #${index + 1}
                            Date/time: ${item.dtTxt}
                            Temperature: ${item.main?.temp} °C
                            Humidity: ${item.main?.humidity}%
                            Description: ${item.weather?.firstOrNull()?.description}
                            """.trimIndent()
                        )
                    }

                    Log.d(
                        "ForecastTest",
                        "Success! Parsed ${forecastItems.size} forecast items."
                    )
                }

            } catch (e: Exception) {
                Log.e("WeatherTest", "Weather request failed", e)
            }
        }

        setContent {
            WeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Testing weather API... Check Logcat.")
                    }
                }
            }
        }
    }
}
