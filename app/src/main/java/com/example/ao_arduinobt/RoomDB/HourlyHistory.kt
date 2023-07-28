package com.example.ao_arduinobt.RoomDB

import androidx.room.ColumnInfo

data class HourlyHistory(
    @ColumnInfo(name = "hour") val hour: String,
    @ColumnInfo(name = "avg_temperature") val avgTemperature: Float,
    @ColumnInfo(name = "avg_humidity") val avgHumidity: Float
)
