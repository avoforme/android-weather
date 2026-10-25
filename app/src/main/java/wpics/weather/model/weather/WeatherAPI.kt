package wpics.weather.model.weather

import androidx.compose.material3.Text
import retrofit2.http.GET
import retrofit2.http.Query
import wpics.weather.BuildConfig

interface WeatherAPI {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String =  BuildConfig.WEATHER_KEY,
        @Query("units") units: UnitSystem,
    ): WeatherResponse

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String =  BuildConfig.WEATHER_KEY,
        @Query("units") units: UnitSystem,
    ): ForecastResponse
}