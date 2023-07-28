package com.example.ao_arduinobt.RoomDB

import androidx.room.ColumnInfo
import java.time.LocalDate

data class DailyHistory(
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "avg_temperature") val avgTemperature: Float,
    @ColumnInfo(name = "avg_humidity") val avgHumidity: Float
)
