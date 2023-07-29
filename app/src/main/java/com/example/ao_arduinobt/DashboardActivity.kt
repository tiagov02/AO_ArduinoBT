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
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardActivity : AppCompatActivity() {
    lateinit var lineGraphViewTime: GraphView
    lateinit var lineGraphViewHourly: GraphView

    val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }

    private val dataPointsTempDaily = mutableListOf<DataPoint>()
    private val dataPointsHumDaily = mutableListOf<DataPoint>()

    private val dataPointsTempHour = mutableListOf<DataPoint>()
    private val dataPointsHumHour = mutableListOf<DataPoint>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        lineGraphViewTime = findViewById(R.id.idGraphView)
        lineGraphViewHourly = findViewById(R.id.idGraphView2)


        retrievefromDBPerDay()
        retrievefromDBPerHour()
    }


    fun retrievefromDBPerDay() {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        historyViewModel.historyPerDay.observe(this) { history ->
            history?.let { data ->
                data.forEach { dt ->
                    dataPointsTempDaily.add(
                        DataPoint(
                            dateFormatter.parse(dt.date),
                            dt.avgTemperature.toDouble()
                        )
                    )
                    dataPointsHumDaily.add(
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

    fun retrievefromDBPerHour() {
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        historyViewModel.historyPerHour.observe(this) { history ->
            history?.let { data ->
                data.forEach { dt ->
                    dataPointsTempHour.add(
                        DataPoint(
                            LocalTime.parse(dt.hour, dateFormatter).toSecondOfDay() * 1000.toLong().toDouble(),
                            dt.avgTemperature.toDouble()
                        )
                    )
                    dataPointsHumHour.add(
                        DataPoint(
                            LocalTime.parse(dt.hour, dateFormatter).toSecondOfDay() * 1000.toLong().toDouble(),
                            dt.avgHumidity.toDouble() * 100
                        )
                    )
                }
                updateGraphPerHour()
            }
        }
    }


    private fun updateGraphPerDay() {
        historyViewModel.historyPerDay.removeObservers(this)
        val seriesTemp: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsTempDaily.toTypedArray())
        val seriesHum: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsHumDaily.toTypedArray())
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


        Log.d("Points:", dataPointsTempDaily.toString())
    }

    private fun updateGraphPerHour() {
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        historyViewModel.historyPerHour.removeObservers(this)
        val seriesTemp: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsTempHour.toTypedArray())
        val seriesHum: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsHumHour.toTypedArray())

        lineGraphViewHourly.animate()
        lineGraphViewHourly.viewport.isScalable = true;

        lineGraphViewHourly.viewport.isScrollable = true;

        seriesTemp.color = R.color.purple_200
        seriesTemp.setDrawDataPoints(true)

        seriesTemp.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toFloat()
            val yValue = dataPoint.y
            val message = "Data: ${xValue}\nValor: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        seriesHum.color = R.color.black
        seriesHum.setDrawDataPoints(true)

        seriesHum.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toFloat()
            val yValue = dataPoint.y
            val message = "Data: ${xValue}\nValor: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        lineGraphViewHourly.addSeries(seriesTemp)
        lineGraphViewHourly.addSeries(seriesHum)

        val labelsFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    val localTime = LocalTime.ofSecondOfDay((value / 1000).toLong())
                    localTime.format(dateFormatter)
                } else {
                    super.formatLabel(value, isValueX)
                }
            }
        }

        lineGraphViewHourly.gridLabelRenderer.labelFormatter = labelsFormatter

        // Definir os rótulos do eixo X em intervalos fixos (opcional)
        lineGraphViewHourly.viewport.isXAxisBoundsManual = true

        // Redesenhar o gráfico
        lineGraphViewHourly.onDataChanged(false, false)

        Log.d("Points:", dataPointsHumHour.toString())
    }

}