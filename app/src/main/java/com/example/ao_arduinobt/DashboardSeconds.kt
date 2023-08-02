package com.example.ao_arduinobt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ao_arduinobt.RoomDB.HistoryAplication
import com.example.ao_arduinobt.RoomDB.HistoryViewModel
import com.example.ao_arduinobt.RoomDB.HistoryViewModelFactory
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardSeconds : AppCompatActivity() {

    lateinit var lineGraphView: GraphView

    val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }
    private var dataPointsTemp = mutableListOf<DataPoint>()
    private var dataPointsHum = mutableListOf<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_seconds)

        lineGraphView = findViewById(R.id.graph_30_seconds)
        lineGraphView.title ="Humidity and Temperature per Every 30 seconds(Average)"

        retrievefromDBPerHourMinuteSecond()
    }

    fun retrievefromDBPerHourMinuteSecond() {
        var lastAddedTime: LocalTime? = null
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        historyViewModel.historyPerHourMinuteSecond.observe(this) { history ->
            dataPointsTemp = mutableListOf()
            dataPointsHum = mutableListOf()
            history?.let { data ->
                data.forEach { dt ->
                    if(lastAddedTime == null){
                        val time = LocalTime.parse(dt.hour, dateFormatter)
                        lastAddedTime = time
                        dataPointsTemp.add(
                            DataPoint(
                                time.toSecondOfDay().toLong().toDouble(),
                                dt.avgTemperature.toDouble()
                            )
                        )
                        dataPointsHum.add(
                            DataPoint(
                                time.toSecondOfDay().toLong().toDouble(),
                                dt.avgHumidity.toDouble() * 100
                            )
                        )
                    }
                    else{
                        val time = LocalTime.parse(dt.hour,dateFormatter)
                        if(isDifferenceBiggerThen30Seconds(lastAddedTime!!, time)){
                            lastAddedTime = time
                            dataPointsTemp.add(
                                DataPoint(
                                    time.toSecondOfDay().toLong().toDouble(),
                                    dt.avgTemperature.toDouble()
                                )
                            )
                            dataPointsHum.add(
                                DataPoint(
                                    time.toSecondOfDay().toLong().toDouble(),
                                    dt.avgHumidity.toDouble() * 100
                                )
                            )
                        }
                    }
                }
                updateGraphPerHourMinuteSecond()
            }
        }
    }

    fun isDifferenceBiggerThen30Seconds(localTime1: LocalTime, localTime2: LocalTime): Boolean {
        val duration = Duration.between(localTime1, localTime2)
        return (duration.seconds >= 30)
    }

    private fun updateGraphPerHourMinuteSecond() {
        historyViewModel.historyPerHourMinuteSecond.removeObservers(this)
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        lineGraphView.removeAllSeries()
        val seriesTemp: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsTemp.toTypedArray())
        val seriesHum: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsHum.toTypedArray())
        seriesHum.title ="Humidity"
        seriesTemp.title= "Temperature"

        lineGraphView.animate()
        lineGraphView.viewport.isScalable = true;

        lineGraphView.viewport.isScrollable = true;

        seriesTemp.color = R.color.purple_200
        seriesTemp.setDrawDataPoints(true)

        seriesTemp.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toFloat()
            val yValue = dataPoint.y
            val localTime = LocalTime.ofSecondOfDay(xValue.toLong())
            val message = "Hour: ${localTime.format(dateFormatter)}\nTemperature: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        seriesHum.color = R.color.black
        seriesHum.setDrawDataPoints(true)

        seriesHum.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toFloat()
            val yValue = dataPoint.y
            val localTime = LocalTime.ofSecondOfDay(xValue.toLong())
            val message = "Hour: ${localTime.format(dateFormatter)}\nHumidity: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        lineGraphView.addSeries(seriesTemp)
        lineGraphView.addSeries(seriesHum)

        val labelsFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    val localTime = LocalTime.ofSecondOfDay(value.toLong())
                    localTime.format(dateFormatter)
                } else {
                    super.formatLabel(value, isValueX)
                }
            }
        }

        lineGraphView.gridLabelRenderer.labelFormatter = labelsFormatter
        lineGraphView.legendRenderer.isVisible = true
        lineGraphView.legendRenderer.align = LegendRenderer.LegendAlign.TOP

        lineGraphView.viewport.isXAxisBoundsManual = true

        lineGraphView.viewport.setMinX(lineGraphView.viewport.getMinX(true))
        lineGraphView.viewport.setMaxX(lineGraphView.viewport.getMaxX(true))

        Log.d("Points:", dataPointsHum.toString())
    }
}