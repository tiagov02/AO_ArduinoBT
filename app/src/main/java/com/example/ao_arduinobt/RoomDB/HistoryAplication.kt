package com.example.ao_arduinobt.RoomDB

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HistoryAplication: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { HistoryDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { HistoryRepository(database.historyDao()) }
}