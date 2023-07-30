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
import com.jjoe64.graphview.helper.StaticLabelsFormatter
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

    private var dataPointsTempDaily = mutableListOf<DataPoint>()
    private var dataPointsHumDaily = mutableListOf<DataPoint>()

    private var dataPointsTempHour = mutableListOf<DataPoint>()
    private var dataPointsHumHour = mutableListOf<DataPoint>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        lineGraphViewTime = findViewById(R.id.idGraphView)
        lineGraphViewHourly = findViewById(R.id.idGraphView2)

        lineGraphViewTime.title = "Humidity and Temperature per Data(Average)"
        lineGraphViewHourly.title = "Humidity and Temperature per Hour(Average)"



        retrievefromDBPerDay()
        retrievefromDBPerHour()
    }


    //Add the points
    fun retrievefromDBPerDay() {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        historyViewModel.historyPerDay.observe(this) { history ->
            dataPointsTempDaily = mutableListOf()
            dataPointsHumDaily = mutableListOf()

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
            dataPointsTempHour = mutableListOf()
            dataPointsHumHour = mutableListOf()
            history?.let { data ->
                data.forEach { dt ->
                    dataPointsTempHour.add(
                        DataPoint(
                            LocalTime.parse(dt.hour, dateFormatter).toSecondOfDay().toLong().toDouble(),
                            dt.avgTemperature.toDouble()
                        )
                    )
                    dataPointsHumHour.add(
                        DataPoint(
                            LocalTime.parse(dt.hour, dateFormatter).toSecondOfDay().toLong().toDouble(),
                            dt.avgHumidity.toDouble() * 100
                        )
                    )
                }
                updateGraphPerHour()
            }
        }
    }
//

    private fun updateGraphPerDay() {
        //historyViewModel.historyPerDay.removeObservers(this)
        lineGraphViewTime.removeAllSeries()
        val dtFormatter = SimpleDateFormat("dd-MM-yy")

        val seriesTemp: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsTempDaily.toTypedArray())
        val seriesHum: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsHumDaily.toTypedArray())
        //format the data
        val xValues = dataPointsTempDaily.mapIndexed { index, _ -> index.toDouble() }
        val xLabels = dataPointsTempDaily.map { dtFormatter.format(it.x) }.toTypedArray()

        // Points with converted Dates
        seriesTemp.resetData(xValues.zip(dataPointsTempDaily).map { DataPoint(it.first, it.second.y) }.toTypedArray())
        seriesHum.resetData(xValues.zip(dataPointsHumDaily).map { DataPoint(it.first, it.second.y) }.toTypedArray())

        //define the title
        seriesHum.title ="Humidity"
        seriesTemp.title= "Temperature"

        lineGraphViewTime.animate()

        lineGraphViewTime.viewport.isScalable = true

        lineGraphViewTime.viewport.isScrollable = true

        seriesTemp.color = R.color.purple_200
        seriesTemp.setDrawDataPoints(true)

        seriesTemp.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toInt()
            val yValue = dataPoint.y
            val dateString = xLabels[xValue]
            val message = "Date: $dateString\nHumidity: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        seriesHum.color = R.color.black
        seriesHum.setDrawDataPoints(true)

        seriesHum.setOnDataPointTapListener { series, dataPoint ->
            val xValue = dataPoint.x.toInt()
            val yValue = dataPoint.y
            val dateString = xLabels[xValue]
            val message = "Date: $dateString\nHumidity: ${yValue.toString()}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        //add the series to teh graph
        lineGraphViewTime.addSeries(seriesTemp)
        lineGraphViewTime.addSeries(seriesHum)

        //format the labels
        val customLabelFormatter = StaticLabelsFormatter(lineGraphViewTime)

        customLabelFormatter.setHorizontalLabels(xLabels)
        lineGraphViewTime.gridLabelRenderer.labelFormatter = customLabelFormatter

        //enable legend
        lineGraphViewTime.legendRenderer.isVisible = true
        lineGraphViewTime.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        lineGraphViewTime.viewport.isXAxisBoundsManual = true

        //set the view port
        lineGraphViewTime.viewport.setMinX(lineGraphViewTime.viewport.getMinX(true))
        lineGraphViewTime.viewport.setMaxX(lineGraphViewTime.viewport.getMaxX(true))


        Log.d("Points:", dataPointsTempDaily.toString())
    }

    private fun updateGraphPerHour() {
        val dateFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        //historyViewModel.historyPerHour.removeObservers(this)
        lineGraphViewHourly.removeAllSeries()
        val seriesTemp: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsTempHour.toTypedArray())
        val seriesHum: LineGraphSeries<DataPoint> = LineGraphSeries(dataPointsHumHour.toTypedArray())
        seriesHum.title ="Humidity"
        seriesTemp.title= "Temperature"

        lineGraphViewHourly.animate()
        lineGraphViewHourly.viewport.isScalable = true;

        lineGraphViewHourly.viewport.isScrollable = true;

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
        lineGraphViewHourly.addSeries(seriesTemp)
        lineGraphViewHourly.addSeries(seriesHum)

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

        lineGraphViewHourly.gridLabelRenderer.labelFormatter = labelsFormatter
        lineGraphViewHourly.legendRenderer.isVisible = true
        lineGraphViewHourly.legendRenderer.align = LegendRenderer.LegendAlign.TOP

        lineGraphViewHourly.viewport.isXAxisBoundsManual = true

        lineGraphViewHourly.viewport.setMinX(lineGraphViewHourly.viewport.getMinX(true))
        lineGraphViewHourly.viewport.setMaxX(lineGraphViewHourly.viewport.getMaxX(true))

        Log.d("Points:", dataPointsHumHour.toString())
    }

}