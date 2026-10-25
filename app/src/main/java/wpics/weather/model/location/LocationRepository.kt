package wpics.weather.model.location

interface LocationRepository {
    suspend fun getLastKnownOrCurrentLocation(): Coordinate?

}