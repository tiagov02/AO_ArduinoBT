package com.example.ao_arduinobt

import com.example.ao_arduinobt.Adapter.AllHistoryAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ao_arduinobt.RoomDB.History

class ShowAllActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all)

        var dataList = listOf(History(12f, 12f))
        val recyclerView = findViewById<RecyclerView>(R.id.recView)
        val adapter = AllHistoryAdapter(dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

}