package com.example.assignment2

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET

@Dao
interface WeatherDao {
    @Upsert
    suspend fun upsertWeather(weather: Weather)

    @Query("SELECT * FROM weatherTable WHERE dayDate = :dayDate AND monthDate = :monthDate AND yearDate = :yearDate")
    fun getWeatherByDate(dayDate: String, monthDate: String, yearDate: String): Weather

    @Query("SELECT * FROM weatherTable WHERE dayDate = :dayDate AND monthDate = :monthDate")
    fun getWeatherByDayAndMonth(dayDate: String, monthDate: String): List<Weather>

}

interface WeatherApiService {
    @GET("archive")
    suspend fun getWeatherData(
        @retrofit2.http.Query("latitude") latitude: Double,
        @retrofit2.http.Query("longitude") longitude: Double,
        @retrofit2.http.Query("start_date") startDate: String,
        @retrofit2.http.Query("end_date") endDate: String,
        @retrofit2.http.Query("daily") daily: List<String>,
        @retrofit2.http.Query("timezone") timezone: String
    ): Response<JsonObject>
}
