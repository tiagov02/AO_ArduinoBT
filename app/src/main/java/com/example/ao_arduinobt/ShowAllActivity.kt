package com.example.ao_arduinobt

import CustomAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ao_arduinobt.RoomDB.History
import com.example.ao_arduinobt.RoomDB.HistoryAplication

class ShowAllActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all)

        var dataList = listOf(History(12f, 12f))
        val recyclerView = findViewById<RecyclerView>(R.id.recView)
        val adapter = CustomAdapter(dataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

}