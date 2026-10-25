package wpics.weather.model.weather

import com.squareup.moshi.Json

data class ForecastResponse(
    @Json(name = "list") val list: List<ForecastItem>? = null,
)

data class ForecastItem(
    @Json(name = "dt_txt") val dtTxt: String? = null,
    @Json(name = "main") val main: Main? = null,
    @Json(name = "weather") val weather: List<Weather>? = null
)



