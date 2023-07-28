package com.example.ao_arduinobt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ao_arduinobt.RoomDB.HistoryAplication
import com.example.ao_arduinobt.RoomDB.HistoryViewModel
import com.example.ao_arduinobt.RoomDB.HistoryViewModelFactory
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.ZoneOffset

class DashboardActivity : AppCompatActivity() {
    lateinit var lineGraphView: GraphView
    lateinit var lineGraphView1: GraphView

    val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        lineGraphView = findViewById(R.id.idGraphView)
        lineGraphView1 = findViewById(R.id.idGraphView2)


        retrievefromDB()
        retrievefromDBH()
    }


    fun retrievefromDB() {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        historyViewModel.historyPerDay.observe(this) { history ->
            history?.let { data ->
                val dataPoints = mutableListOf<DataPoint>()

                data.forEach { dt ->
                    dataPoints.add(
                        DataPoint(
                            dateFormatter.parse(dt.date),
                            dt.avgTemperature.toDouble()
                        )
                    )
                }

                updateGraph(dataPoints)
            }
        }
    }

    private fun updateGraph(dataPoints: List<DataPoint>) {

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(dataPoints.toTypedArray())
        lineGraphView.animate()

        lineGraphView.viewport.isScalable = true;

        lineGraphView.viewport.isScrollable = true;

        series.color = R.color.purple_200
        series.setDrawDataPoints(true)
        lineGraphView.addSeries(series)

        Log.d("Points:", dataPoints.toString())
    }


    fun retrievefromDBH() {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        historyViewModel.historyPerDay.observe(this) { history ->
            history?.let { data ->
                val dataPoints = mutableListOf<DataPoint>()

                data.forEach { dt ->
                    dataPoints.add(
                        DataPoint(
                            dateFormatter.parse(dt.date),
                            dt.avgHumidity.toDouble()
                        )
                    )
                }

                updateGraphHumidity(dataPoints)
            }
        }
    }

    private fun updateGraphHumidity(dataPoints: List<DataPoint>) {

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(dataPoints.toTypedArray())

        lineGraphView1.animate()
        lineGraphView1.viewport.isScalable = true;

        lineGraphView1.viewport.isScrollable = true;
        series.color = R.color.purple_200
        series.isDrawDataPoints = true
        lineGraphView1.addSeries(series)
        lineGraphView1.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)

        Log.d("Points:", dataPoints.toString())
    }


}