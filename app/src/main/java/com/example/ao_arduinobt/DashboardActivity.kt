package com.example.ao_arduinobt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ao_arduinobt.RoomDB.HistoryAplication
import com.example.ao_arduinobt.RoomDB.HistoryViewModel
import com.example.ao_arduinobt.RoomDB.HistoryViewModelFactory
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat

class DashboardActivity : AppCompatActivity() {
    lateinit var lineGraphViewTime: GraphView
    lateinit var lineGraphViewHourly: GraphView

    val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }

    private val dataPointsTemp = mutableListOf<DataPoint>()
    private val dataPointsHum = mutableListOf<DataPoint>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        lineGraphViewTime = findViewById(R.id.idGraphView)
        lineGraphViewHourly = findViewById(R.id.idGraphView2)


        retrievefromDBPerDay()
    }


    fun retrievefromDBPerDay() {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        historyViewModel.historyPerDay.observe(this) { history ->
            history?.let { data ->
                data.forEach { dt ->
                    dataPointsTemp.add(
                        DataPoint(
                            dateFormatter.parse(dt.date),
                            dt.avgTemperature.toDouble()
                        )
                    )
                    dataPointsHum.add(
                        DataPoint(
                            dateFormatter.parse(dt.date),
                            dt.avgHumidity.toDouble() * 100
                        )
                    )
                }
                updateGraphPerDay()
            }
        }
    }


    private fun updateGraphPerDay() {
        historyViewModel.historyPerDay.removeObservers(this)
        val seriesTemp: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsTemp.toTypedArray())
        val seriesHum: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsHum.toTypedArray())
        val dtFormatter = SimpleDateFormat("dd/MM/yyyy")

        lineGraphViewTime.animate()

        lineGraphViewTime.viewport.isScalable = true

        lineGraphViewTime.viewport.isScrollable = true

        seriesTemp.color = R.color.purple_200
        seriesTemp.setDrawDataPoints(true)

        seriesTemp.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toFloat()
            val yValue = dataPoint.y
            val message = "Data: ${dtFormatter.format(xValue)}\nValor: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        seriesHum.color = R.color.black
        seriesHum.setDrawDataPoints(true)

        seriesHum.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toFloat()
            val yValue = dataPoint.y
            val message = "Data: ${dtFormatter.format(xValue)}\nValor: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        lineGraphViewTime.addSeries(seriesTemp)
        lineGraphViewTime.addSeries(seriesHum)
        lineGraphViewTime.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)


        Log.d("Points:", dataPointsTemp.toString())
    }

    /*private fun updateGraphHumidity(dataPoints: List<DataPoint>) {

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(dataPoints.toTypedArray())

        lineGraphView1.animate()
        lineGraphView1.viewport.isScalable = true;

        lineGraphView1.viewport.isScrollable = true;
        series.color = R.color.purple_200
        series.isDrawDataPoints = true
        lineGraphView1.addSeries(series)
        lineGraphView1.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)

        Log.d("Points:", dataPoints.toString())
    }*/

}