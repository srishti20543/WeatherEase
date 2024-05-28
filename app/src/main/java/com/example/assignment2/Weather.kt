package com.example.assignment2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weatherTable", primaryKeys = ["dayDate", "monthDate", "yearDate"])
data class Weather(
    val dayDate: String,
    val monthDate: String,
    val yearDate: String,
    val maxTemp: String,
    val minTemp: String
)

data class WeatherEntry(
    val daily: Daily
)

data class Daily(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)
