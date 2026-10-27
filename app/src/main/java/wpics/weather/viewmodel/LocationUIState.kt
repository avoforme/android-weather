// Thanh Ho
package wpics.weather.viewmodel

import wpics.weather.model.location.Coordinate

data class LocationUIState(
    val hasLocationPermission: Boolean = false,
    val isLoading: Boolean = false,
    val coordinate: Coordinate? = null
)