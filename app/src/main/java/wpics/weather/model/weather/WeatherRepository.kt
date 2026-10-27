// Thanh Ho
package wpics.weather.model.weather

import wpics.weather.model.location.Coordinate

class WeatherRepository (
    private val api: WeatherAPI = RetrofitProvider.api
) {
    suspend fun getWeather(
        coordinate: Coordinate,
        units: UnitSystem = UnitSystem.METRIC
    ): WeatherResponse {
        return api.getWeather(
            lat = coordinate.lat,
            lon = coordinate.lon,
            units = units
        )
    }

    suspend fun getForecast(
        coordinate: Coordinate,
        units: UnitSystem = UnitSystem.METRIC
    ): ForecastResponse {
        return api.getForecast(
            lat = coordinate.lat,
            lon = coordinate.lon,
            units = units
        )
    }
}