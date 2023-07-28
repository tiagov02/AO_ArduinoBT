package com.example.ao_arduinobt.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Query("SELECT * FROM history ORDER BY date_time_measure DESC")
    fun getHistoryOrdered(): Flow<List<History>>

    @Query("SELECT * FROM history ORDER BY date_time_measure ASC")
    fun getHistoryAsc(): Flow<List<History>>

    @Query("SELECT DATE(date_time_measure) AS date, AVG(temperature) AS avg_temperature, AVG(humidity) AS avg_humidity FROM history GROUP BY DATE(date_time_measure)")
    fun getAverageHistoryPerDay(): Flow<List<DailyHistory>>
}