package wpics.weather.model.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PlayServicesLocationRepository(
    private val context: Context
) : LocationRepository {

    companion object {
        private const val TAG = "WeatherApp"
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override suspend fun getLastKnownOrCurrentLocation(): Coordinate? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        return try {
            suspendCancellableCoroutine { continuation ->

                // First, try to get the device's last known location.
                client.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            if (continuation.isActive) {
                                continuation.resume(
                                    Coordinate(location.latitude, location.longitude)
                                )
                            }
                        } else {
                            // If no last known location exists, request a current location.
                            val cancellationTokenSource = CancellationTokenSource()

                            continuation.invokeOnCancellation {
                                cancellationTokenSource.cancel()
                            }

                            client.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                cancellationTokenSource.token
                            )
                                .addOnSuccessListener { currentLocation ->
                                    if (continuation.isActive) {
                                        continuation.resume(
                                            currentLocation?.let {
                                                Coordinate(it.latitude, it.longitude)
                                            }
                                        )
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(TAG, "Failed to get current location", exception)
                                    if (continuation.isActive) {
                                        continuation.resume(null)
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to get last known location", exception)
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
            }
        } catch (exception: SecurityException) {
            Log.e(TAG, "Location permission is missing", exception)
            null
        }
    }
}