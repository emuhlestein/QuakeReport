package com.intelliviz.quakereport.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intelliviz.quakereport.EarthquakeTrendViewModel
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.R
import com.intelliviz.quakereport.db.Earthquake
import com.intelliviz.quakereport.graphview.GraphView
import java.util.*

class EarthquakeTrendFragment: Fragment() {
    private lateinit var viewModel: EarthquakeTrendViewModel
    companion object {
        fun newInstance(): EarthquakeTrendFragment {
            val fragment = EarthquakeTrendFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_trend_fragment, container, false)

        val earthquakeGraphView = view.findViewById<GraphView>(R.id.earthquakeGraphView)
//        earthquakeGraphView.setMinMaxX(1900F, 2018F)
//        earthquakeGraphView.setXInc(20)
//        earthquakeGraphView.setMinMaxY(0F, 9F)
//        earthquakeGraphView.setYInc(1)

//        var y: FloatArray = floatArrayOf(20F, 1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F, 9F, 10F)
//        var x: FloatArray = floatArrayOf(1900F, 1910F, 1935F, 1950F, 1960F, 1970F, 1975F, 1985F, 1995F, 2000F, 2010F)
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

        val year: Int = QueryPreferences.getYear(context!!)
        val minMag: Int = QueryPreferences.getMinMag(context!!)
        val maxMag: Int = QueryPreferences.getMaxMag(context!!)

        val factory: EarthquakeTrendViewModel.Factory = EarthquakeTrendViewModel.Factory(activity!!.application, year, minMag, maxMag)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeTrendViewModel::class.java)
        viewModel.getEarthquakes()?.observe(this, earthquakeObserver)
        viewModel.loadEarthquakes(year, minMag, maxMag)


        return view
    }
}