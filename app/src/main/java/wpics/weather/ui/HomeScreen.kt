
package wpics.weather

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import wpics.weather.viewmodel.LocationViewModel

@Composable
fun HomeScreen(
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current
    val locationState by locationViewModel.uiState.collectAsState()

    var permissionChecked by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }

            permissionChecked = true
            permissionDenied = !granted

            locationViewModel.updatePermissionStatus(granted)
        }

    LaunchedEffect(Unit) {
        val hasPermission = locationPermissions.any { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        permissionChecked = true

        if (hasPermission) {
            permissionDenied = false
            locationViewModel.updatePermissionStatus(true)
        } else {
            permissionLauncher.launch(locationPermissions)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            !permissionChecked -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Checking location permission...")
            }

            permissionDenied ||
                    !locationState.hasLocationPermission -> {
                Text(
                    text = "Location permission is needed to show " +
                            "the weather for your current location.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        permissionLauncher.launch(locationPermissions)
                    }
                ) {
                    Text("Grant permission")
                }
            }

            locationState.isLoading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Getting your location...")
            }

            locationState.coordinate == null -> {
                Text(
                    text = "Couldn't get your location. " +
                            "Please try again."
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        locationViewModel.loadLocation()
                    }
                ) {
                    Text("Retry")
                }
            }
        }
    }
}
