
package wpics.weather.model.weather

import com.squareup.moshi.Json

data class ForecastResponse(
    val list: List<ForecastItem>? = null,
    val city: ForecastCity? = null
)

data class ForecastCity(
    val name: String? = null
)

data class ForecastItem(
    @Json(name = "dt_txt") val dtTxt: String? = null,
    val main: Main? = null,
    val weather: List<Weather>? = null
)


data class ForecastDay(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String?,
    val description: String?
)
