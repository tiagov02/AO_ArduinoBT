package com.example.ao_arduinobt.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ao_arduinobt.R
import com.example.ao_arduinobt.RoomDB.History

class AllHistoryAdapter() : ListAdapter<History, AllHistoryAdapter.AllHistoryViewHolder>(WORDS_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllHistoryViewHolder {
        return AllHistoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: AllHistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class AllHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.textDate)
        val textView2: TextView = itemView.findViewById(R.id.textTemp)
        val textView3: TextView = itemView.findViewById(R.id.textHumidity)

        fun bind(elem: History) {
            textView1.text = elem.date_time_measure.toString()
            textView2.text = elem.temperature.toString()
            textView3.text = elem.humidity.toString()
        }
        companion object {
            fun create(parent: ViewGroup): AllHistoryViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler, parent, false)
                return AllHistoryViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<History>() {
            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }
        }
    }
}
