// Thanh Ho
package wpics.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import wpics.weather.model.location.PlayServicesLocationRepository
import wpics.weather.model.weather.WeatherRepository
import wpics.weather.ui.WeatherScreen
import wpics.weather.ui.theme.WeatherTheme
import wpics.weather.viewmodel.LocationViewModel
import wpics.weather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val locationRepository =
            PlayServicesLocationRepository(applicationContext)
        val weatherRepository = WeatherRepository()

        val locationFactory = viewModelFactory {
            initializer {
                LocationViewModel(locationRepository)
            }
        }

        val weatherFactory = viewModelFactory {
            initializer {
                WeatherViewModel(weatherRepository)
            }
        }

        locationViewModel = ViewModelProvider(
            this, locationFactory
        )[LocationViewModel::class.java]

        weatherViewModel = ViewModelProvider(
            this, weatherFactory
        )[WeatherViewModel::class.java]

        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            WeatherTheme(darkTheme = darkTheme) {
                val locationState by
                locationViewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Appearance: ${
                                    if (darkTheme) "Dark" else "Light"
                                }",
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    darkTheme = !darkTheme
                                }
                            ) {
                                Text(
                                    text = if (darkTheme) {
                                        "Light Theme"
                                    } else {
                                        "Dark Theme"
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            if (locationState.coordinate != null) {
                                WeatherScreen(
                                    weatherViewModel = weatherViewModel,
                                    coordinate = locationState.coordinate
                                )
                            } else {
                                HomeScreen(
                                    locationViewModel = locationViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
