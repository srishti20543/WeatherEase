package com.example.assignment2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.assignment2.ui.theme.Assignment2Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weatherDatabase: WeatherDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the AppDatabase instance
        weatherDatabase = WeatherDatabase.getDatabase(applicationContext)

        // Create the NoteViewModel instance, passing the NoteDao from AppDatabase as a parameter
        weatherViewModel = WeatherViewModel(weatherDatabase.dao())

        setContent {
            Assignment2Theme(true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp(weatherViewModel, applicationContext)
                }
            }
        }
    }
}

@Composable
fun WeatherApp(weatherViewModel: WeatherViewModel, applicationContext: Context) {
    var date by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf("") }
    var maxTemp by remember { mutableStateOf("") }
    var minTemp by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Weather App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "ISTANBUL, TURKEY",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = {
                // Handle keyboard done
            }),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                fetchWeather(date.text, weatherViewModel, applicationContext) { error, max, min ->
                    errorMessage = error
                    maxTemp = max
                    minTemp = min
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Weather Data")
        }

        if (maxTemp.isNotEmpty() && minTemp.isNotEmpty()) {
            Text(
                text = "Max Temperature: ${maxTemp}°C",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Min Temperature: ${minTemp}°C",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun fetchWeather(
    date: String,
    weatherViewModel: WeatherViewModel,
    applicationContext: Context,
    callback: (String, String, String) -> Unit
) {
    if (!validateDate(date)) {
        callback("Invalid Date", "", "")
        return
    }

    val parts = date.split("-")
    val year = parts[0].toIntOrNull()
    val month = parts[1].toIntOrNull()
    val day = parts[2].toIntOrNull()
    if (year == null || month == null || day == null) {
        callback("Invalid Date", "", "")
        return
    }

    val weatherAPI = RetrofitHelper.getInstance().create(WeatherApiService::class.java)
    GlobalScope.launch {

        if (isDateInFuture(year, month, day)) {
            val returnValue = getFutureDate(day.toString(), month.toString(), weatherViewModel)
            callback(returnValue[0], returnValue[1], returnValue[2])
        } else {

            if (isNetworkConnected(applicationContext)) {
                val result = weatherAPI.getWeatherData(
                    41.0082,
                    28.9784,
                    date,
                    date,
                    listOf("temperature_2m_max", "temperature_2m_min"),
                    "GMT"
                )

                if (result.isSuccessful) {
                    val gson = Gson()
                    val weatherResponse =
                        gson.fromJson(result.body().toString(), WeatherEntry::class.java)

                    val maxTemperatures = weatherResponse.daily.temperature_2m_max[0].toString()
                    val minTemperatures = weatherResponse.daily.temperature_2m_min[0].toString()

                    weatherViewModel.insert(
                        Weather(
                            dayDate = day.toString(),
                            monthDate = month.toString(),
                            yearDate = year.toString(),
                            maxTemp = maxTemperatures,
                            minTemp = minTemperatures
                        )
                    )
                    callback("", maxTemperatures, minTemperatures)
                } else {
                    callback("Error Fetching Date from API", "", "")
                }
            } else {
                try {
                    val weather = weatherViewModel.getWeather(
                        day.toString(),
                        month.toString(),
                        year.toString()
                    )
                    callback("", weather.maxTemp, weather.minTemp)
                } catch (e: Exception) {
                    callback("Internet is not Connected & Date not found in database", "", "")
                }
            }
        }
    }
    return
}
