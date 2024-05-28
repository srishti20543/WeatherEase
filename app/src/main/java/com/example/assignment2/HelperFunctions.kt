package com.example.assignment2

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

fun validateDate(date: String): Boolean {
    if (date.isEmpty()) {
        return false
    }

    val parts = date.split("-")
    if (parts.size != 3) {
        return false
    }

    val year = parts[0].toIntOrNull()
    val month = parts[1].toIntOrNull()
    val day = parts[2].toIntOrNull()
    if (year == null || month == null || day == null) {
        return false
    }

    if (year < 0 || month !in 1..12 || day !in 1..31) {
        return false
    }

    val daysInMonth = if (month in intArrayOf(1, 3, 5, 7, 8, 10, 12)) {
        31
    } else if (month in intArrayOf(4, 6, 9, 11)) {
        30
    } else {
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    }
    return day <= daysInMonth
}

fun isDateInFuture(year: Int, month: Int, day: Int): Boolean {
    val currentDate = LocalDate.now()
    val dateToCheck = LocalDate.of(year, month, day)
    return dateToCheck.isAfter(currentDate) || dateToCheck.isEqual(currentDate)
}

fun getFutureDate(
    day: String,
    month: String,
    weatherViewModel: WeatherViewModel
): List<String> {

    val dayMonths = weatherViewModel.getFutureWeather(day, month)
    if (dayMonths.isEmpty()) {
        return listOf("No dates for $day-$month in database", "", "")
    }
    val sortedDayMonths: List<Weather> = if (dayMonths.size > 10){
        dayMonths.sortedByDescending { it.yearDate }.take(10)
    }
    else{
        dayMonths
    }
    var avgMaxTemp = 0.0
    var avgMinTemp = 0.0
    for (dayMonth in sortedDayMonths.listIterator()) {
        avgMaxTemp += dayMonth.maxTemp.toDouble()
        avgMinTemp += dayMonth.minTemp.toDouble()
    }
    avgMaxTemp /= sortedDayMonths.size
    avgMinTemp /= sortedDayMonths.size

    return listOf("", "%.1f".format(avgMaxTemp), "%.1f".format(avgMinTemp))
}

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    }
}

object RetrofitHelper {
    private const val baseUrl = "https://archive-api.open-meteo.com/v1/"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
