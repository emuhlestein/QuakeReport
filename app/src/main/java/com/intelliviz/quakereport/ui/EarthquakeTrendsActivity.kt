package com.intelliviz.quakereport.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.intelliviz.quakereport.Earthquake
import com.intelliviz.quakereport.EarthquakeTrendViewModel
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.R
import com.intelliviz.quakereport.graphview.GraphView

class EarthquakeTrendsActivity : AppCompatActivity() {
    private lateinit var viewModel: EarthquakeTrendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earthquake_trends)

        val toolbar = findViewById<Toolbar>(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        val earthquakeGraphView = findViewById<GraphView>(R.id.earthquakeGraphView)

        var y: FloatArray = floatArrayOf(20F, 10F)
        var x: FloatArray = floatArrayOf(1900F, 2010F)
        earthquakeGraphView.setData(x, y)
        earthquakeGraphView.setVerticalLabel("Number of quakes")
        earthquakeGraphView.setHorizontalLabel("Year")


        var spotPaint = Paint()
        spotPaint.color = Color.GREEN
        earthquakeGraphView.setSpotColor(spotPaint)

        var legendValues = mutableListOf(6F, 7F, 8F, 9F)

        var valueColors = mutableListOf<Paint>()

        var paint = Paint()
        paint.color = Color.BLUE
        valueColors.add(paint)

        paint = Paint()
        paint.color = Color.GREEN
        valueColors.add(paint)

        paint = Paint()
        paint.color = Color.MAGENTA
        valueColors.add(paint)

        paint = Paint()
        paint.color = Color.RED
        valueColors.add(paint)

        earthquakeGraphView.setLegendValues(legendValues, valueColors)


        val earthquakeObserver = Observer<List<Earthquake>> { earthquakes ->
            val earthquakeData =  ArrayList<Earthquake>(earthquakes)
            //adapter.addAll(earthquakeData)
        }

        val year: Int = QueryPreferences.getYear(this)
        val minMag: Int = QueryPreferences.getMinMag(this)
        val maxMag: Int = QueryPreferences.getMaxMag(this)

        val factory: EarthquakeTrendViewModel.Factory = EarthquakeTrendViewModel.Factory(application, year, minMag, maxMag)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeTrendViewModel::class.java)
        viewModel.earthquakes?.observe(this, earthquakeObserver)
        viewModel.loadEarthquakes(year, minMag, maxMag)

    }

}
