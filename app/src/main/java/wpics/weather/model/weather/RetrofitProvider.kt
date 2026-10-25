package wpics.weather.model.weather

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitProvider {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory()).build()

    // Logging interceptor to help us log our API calls
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Attach it to the explicit client we created
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging) // This makes calls visible in Logcat and Inspector
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()


    val api: WeatherAPI = retrofit.create(WeatherAPI::class.java)
}