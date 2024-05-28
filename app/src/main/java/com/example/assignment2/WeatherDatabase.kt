package com.example.assignment2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Weather::class],
    version = 1
)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun dao(): WeatherDao

    companion object {
        @Volatile
        private var Instance: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WeatherDatabase::class.java, "weatherTable")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}