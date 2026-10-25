package wpics.weather

import wpics.weather.ui.WeatherScreen
import wpics.weather.viewmodel.LocationViewModel
import wpics.weather.viewmodel.WeatherViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import wpics.weather.model.location.PlayServicesLocationRepository
import wpics.weather.model.weather.WeatherRepository
import wpics.weather.ui.theme.WeatherTheme

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
            WeatherTheme {
                val locationState by
                locationViewModel.uiState.collectAsState()

                Surface {
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
