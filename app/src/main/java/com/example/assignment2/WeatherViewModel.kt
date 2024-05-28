package com.example.assignment2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherDao: WeatherDao) : ViewModel() {


    fun insert(weather: Weather) = viewModelScope.launch {
        weatherDao.upsertWeather(weather)
    }

    fun getWeather(dayDate: String, monthDate: String, yearDate: String) =  weatherDao.getWeatherByDate(dayDate, monthDate, yearDate)

    fun getFutureWeather(dayDate: String, monthDate: String) = weatherDao.getWeatherByDayAndMonth(dayDate, monthDate)
}