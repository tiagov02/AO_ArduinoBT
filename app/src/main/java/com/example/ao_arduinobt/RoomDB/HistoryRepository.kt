package com.example.ao_arduinobt.RoomDB

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDAO: HistoryDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val history: Flow<List<History>> = historyDAO.getHistoryOrdered()
    val historyPerDay: Flow<List<DailyHistory>> = historyDAO.getAverageHistoryPerDay()
    val historyPerHour: Flow<List<HourlyHistory>> = historyDAO.getAverageHistoryPerHour()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(history: History) {
        historyDAO.insert(history)
    }

}