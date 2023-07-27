package com.example.ao_arduinobt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.ao_arduinobt.RoomDB.HistoryAplication
import com.example.ao_arduinobt.RoomDB.HistoryViewModel
import com.example.ao_arduinobt.RoomDB.HistoryViewModelFactory
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class DashboardActivity : AppCompatActivity() {
    lateinit var lineGraphView: GraphView
    lateinit var lineGraphView1: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        lineGraphView = findViewById(R.id.idGraphView)
        // on below line we are adding data to our graph view.

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(

                DataPoint(0.0, 1.0),
                DataPoint(1.0, 3.0),
                DataPoint(2.0, 4.0),
                DataPoint(3.0, 9.0),
                DataPoint(4.0, 6.0),
                DataPoint(5.0, 3.0),
                DataPoint(6.0, 6.0),
                DataPoint(7.0, 1.0),
                DataPoint(8.0, 2.0)
            )
        )

        // on below line adding animation
        lineGraphView.animate()

        // on below line we are setting scrollable
        // for point graph view
        lineGraphView.viewport.isScrollable = true

        // on below line we are setting scalable.
        lineGraphView.viewport.isScalable = true

        // on below line we are setting scalable y
        lineGraphView.viewport.setScalableY(true)

        // on below line we are setting scrollable y
        lineGraphView.viewport.setScrollableY(true)

        // on below line we are setting color for series.
        series.color = R.color.purple_200

        // on below line we are adding
        // data series to our graph view.
        lineGraphView.addSeries(series)



        lineGraphView1 = findViewById(R.id.idGraphView2)
        // on below line we are adding data to our graph view.

        val series1: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(

                DataPoint(0.0, 1.0),
                DataPoint(1.0, 3.0),
                DataPoint(2.0, 4.0),
                DataPoint(3.0, 9.0),
                DataPoint(4.0, 6.0),
                DataPoint(5.0, 3.0),
                DataPoint(6.0, 6.0),
                DataPoint(7.0, 1.0),
                DataPoint(8.0, 2.0)
            )
        )

        // on below line adding animation
        lineGraphView1.animate()

        // on below line we are setting scrollable
        // for point graph view
        lineGraphView1.viewport.isScrollable = true

        // on below line we are setting scalable.
        lineGraphView1.viewport.isScalable = true

        // on below line we are setting scalable y
        lineGraphView1.viewport.setScalableY(true)

        // on below line we are setting scrollable y
        lineGraphView1.viewport.setScrollableY(true)

        // on below line we are setting color for series.
        series1.color = R.color.purple_200

        // on below line we are adding
        // data series to our graph view.
        lineGraphView1.addSeries(series)

    }
}