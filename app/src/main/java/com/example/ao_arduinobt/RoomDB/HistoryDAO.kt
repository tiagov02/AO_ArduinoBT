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

    @Query("SELECT * FROM history ") //SELECT * FROM history ORDER BY date ASC
    fun getHistoryOrdered(): Flow<List<History>>
}