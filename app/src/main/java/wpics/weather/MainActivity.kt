package wpics.weather

import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import wpics.weather.model.location.PlayServicesLocationRepository
import wpics.weather.model.weather.WeatherRepository
import wpics.weather.ui.theme.WeatherTheme
import wpics.weather.viewmodel.LocationViewModel
import wpics.weather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create each repository once.
        val locationRepository =
            PlayServicesLocationRepository(applicationContext)
        val weatherRepository = WeatherRepository()

        // Create a factory for LocationViewModel.
        val locationViewModelFactory = viewModelFactory {
            initializer {
                LocationViewModel(locationRepository)
            }
        }

        // Create a factory for WeatherViewModel.
        val weatherViewModelFactory = viewModelFactory {
            initializer {
                WeatherViewModel(weatherRepository)
            }
        }

        // Obtain ViewModels using their factories.
        // ViewModelProvider reuses existing ViewModels when appropriate.
        locationViewModel = ViewModelProvider(
            this,
            locationViewModelFactory
        )[LocationViewModel::class.java]

        weatherViewModel = ViewModelProvider(
            this,
            weatherViewModelFactory
        )[WeatherViewModel::class.java]

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
                        Text("Weather app")
                    }
                }
            }
        }
    }
}
