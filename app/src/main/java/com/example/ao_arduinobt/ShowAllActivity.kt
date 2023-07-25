package com.example.ao_arduinobt

import com.example.ao_arduinobt.Adapter.AllHistoryAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ao_arduinobt.RoomDB.History
import com.example.ao_arduinobt.RoomDB.HistoryAplication
import com.example.ao_arduinobt.RoomDB.HistoryViewModel
import com.example.ao_arduinobt.RoomDB.HistoryViewModelFactory
import java.time.LocalDateTime

class ShowAllActivity : AppCompatActivity() {

    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all)

        val num: MutableList<History> = mutableListOf(History(12f, 12f, LocalDateTime.now()))

        // Adding elements to the list
        num.add(History(18f, 12f, LocalDateTime.now()))

        //var dataList = listOf(History(12f, 12f))
        val recyclerView = findViewById<RecyclerView>(R.id.recView)
        val adapter = AllHistoryAdapter(num)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

}