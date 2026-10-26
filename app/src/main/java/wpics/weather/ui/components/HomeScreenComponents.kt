package wpics.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationCheckingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Checking location permission...")
    }
}

@Composable
fun LocationPermissionRequiredContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "To show the weather for your current location, " +
                    "please enable location permission in your device settings.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "How to enable location:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "1. Open Settings on your device.\n" +
                    "2. Go to Apps and select Weather.\n" +
                    "3. Tap Permissions, then Location.\n" +
                    "4. Select \"Allow only while using the app\" " +
                    "or \"Ask every time\".\n" +
                    "5. Return to the Weather app.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LocationLoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Getting your location...")
    }
}

@Composable
fun LocationErrorContent(
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Couldn't get your location. Please try again.")

        Spacer(Modifier.height(16.dp))

        androidx.compose.material3.Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}