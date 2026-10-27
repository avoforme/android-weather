package wpics.weather.model.weather

import com.squareup.moshi.Json

data class WeatherResponse(
    val name: String? = null,
    val main: Main? = null,
    val weather: List<Weather>? = null,
    val wind: Wind? = null,
    val sys: Sys? = null,
    val rain: Rain? = null
)


data class Main(
    val temp: Double? = null,
    val humidity: Int? = null,
    val pressure: Int? = null
)

data class Weather(
    val description: String? = null,
    val icon: String? = null
)

data class Wind(
    val speed: Double? = null
)

data class Sys(
    val sunrise: Long? = null,
    val sunset: Long? = null
)

data class Rain(
    @Json(name = "1h") val oneHour: Double? = null,
)