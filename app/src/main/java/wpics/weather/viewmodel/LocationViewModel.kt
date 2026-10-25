package wpics.weather.viewmodel

import wpics.weather.model.location.LocationRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUIState())
    val uiState: StateFlow<LocationUIState> = _uiState.asStateFlow()

    fun updatePermissionStatus(granted: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = granted
        )

        if (granted) {
            loadLocation()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                coordinate = null
            )
        }
    }

    fun loadLocation() {
        if (!_uiState.value.hasLocationPermission) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            try {
                val coordinate =
                    locationRepository.getLastKnownOrCurrentLocation()

                _uiState.value = _uiState.value.copy(
                    coordinate = coordinate,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    coordinate = null,
                    isLoading = false
                )
            }
        }
    }
}
