package com.example.ao_arduinobt.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ao_arduinobt.R
import com.example.ao_arduinobt.RoomDB.History

class AllHistoryAdapter(private val dataList: List<History>) :
    RecyclerView.Adapter<AllHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.textDate)
        val textView2: TextView = itemView.findViewById(R.id.textTemp)
        val textView3: TextView = itemView.findViewById(R.id.textHumidity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.textView3.text = data.humidity.toString()
        holder.textView2.text = data.temperature.toString()
        //holder.textView1.text = data.text3
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
