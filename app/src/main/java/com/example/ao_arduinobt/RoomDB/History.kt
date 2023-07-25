package com.example.ao_arduinobt.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

//TODO: ver DateTime
@Entity(tableName = "history")
data class History(
    @ColumnInfo(name = "temperature") val temperature: Float,
    @ColumnInfo(name = "humidity") val humidity: Float,
    //add data
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)
