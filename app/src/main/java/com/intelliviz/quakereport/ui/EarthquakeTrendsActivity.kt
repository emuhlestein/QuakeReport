package com.intelliviz.quakereport.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.intelliviz.quakereport.EarthquakeTrendViewData
import com.intelliviz.quakereport.EarthquakeTrendViewModel
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.R
import com.intelliviz.quakereport.graphview.GraphView

class EarthquakeTrendsActivity : AppCompatActivity(), EarthquakeTrendsOptionsDialog.OnTrendOptionsSelectedListener {

    private lateinit var viewModel: EarthquakeTrendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earthquake_trends)

        val toolbar = findViewById<Toolbar>(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        val earthquakeGraphView = findViewById<GraphView>(R.id.earthquakeGraphView)

        earthquakeGraphView.setVerticalLabel("Number of quakes")
        earthquakeGraphView.setHorizontalLabel("Year")

        val earthquakeObserver = Observer<EarthquakeTrendViewData> { earthquake ->
            //val earthquakeData =  ArrayList<EarthquakeTrendViewData>(earthquakes)
            // add data to graphview

            if(!earthquake!!.values.isEmpty()) {
                earthquakeGraphView.setData(earthquake!!.values)
                earthquakeGraphView.setLegendValues(earthquake.colors)
                earthquakeGraphView.invalidate()
                earthquakeGraphView.requestLayout()
            }
        }

        val year: Int = QueryPreferences.getYear(this)
        val minMag: Int = QueryPreferences.getMinMag(this)
        val maxMag: Int = QueryPreferences.getMaxMag(this)

        val factory: EarthquakeTrendViewModel.Factory = EarthquakeTrendViewModel.Factory(application)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeTrendViewModel::class.java)
        viewModel.earthquakeInfo?.observe(this, earthquakeObserver)
        viewModel.loadEarthquakes(year, minMag, maxMag)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.trends_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_item -> {
                val year = QueryPreferences.getYear(this)
                val minMag = QueryPreferences.getMinMag(this)
                val maxMag = QueryPreferences.getMaxMag(this)
                val dialog = EarthquakeTrendsOptionsDialog.newInstance(year, minMag, maxMag)
                dialog.show(supportFragmentManager, "options")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onOptionsSelected(year: Int, minMag: Int, maxMag: Int) {
        viewModel.loadEarthquakes(year, minMag, maxMag)
    }
}
