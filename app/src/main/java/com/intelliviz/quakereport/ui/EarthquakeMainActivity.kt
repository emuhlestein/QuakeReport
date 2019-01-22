package com.intelliviz.quakereport.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.intelliviz.quakereport.EarthquakeAdapter
import com.intelliviz.quakereport.EarthquakeRangeViewModel
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.QueryPreferences.MODE_RANGE
import com.intelliviz.quakereport.R
import com.intelliviz.quakereport.db.Earthquake
import kotlinx.android.synthetic.main.earthquake_range_fragment.*
import java.util.*

class EarthquakeMainActivity : AppCompatActivity(),
        EarthquakeOptionsDialog.OnOptionsSelectedListener {
    private lateinit var viewModel: EarthquakeRangeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_range_fragment)

        val toolbar = findViewById<Toolbar>(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        val earthquakeListView = findViewById<RecyclerView>(R.id.earthquakeListView)

        val earthquakeList = ArrayList<Earthquake>()
        val adapter = EarthquakeAdapter(this, earthquakeList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        earthquakeListView.layoutManager = layoutManager
        earthquakeListView.adapter = adapter

        earthquakeListView.setOnClickListener {
            Toast.makeText(this, "HERE", Toast.LENGTH_LONG).show()
        }

        val earthquakeObserver = Observer<List<Earthquake>> { earthquakes ->
            val earthquakeData = ArrayList<Earthquake>(earthquakes)
            if(earthquakeData.isEmpty()) {
                earthquakeListView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                earthquakeListView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
            adapter.addAll(earthquakeData)
        }

        val mode: Int = QueryPreferences.getMode(this)
        val endDate: String = QueryPreferences.getEndDate(this)
        val startDate: String = QueryPreferences.getStartDate(this)
        val minMag: Int = QueryPreferences.getMinMag(this)
        val maxMag: Int = QueryPreferences.getMaxMag(this)
        val numDays: Int = QueryPreferences.getNumDays(this)

        val factory: EarthquakeRangeViewModel.Factory = EarthquakeRangeViewModel.Factory(this.application, endDate, startDate, minMag, maxMag)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeRangeViewModel::class.java)
        viewModel.getEarthquakes()?.observe(this, earthquakeObserver)
        if(mode == MODE_RANGE) {
            viewModel.loadEarthquakes(endDate, startDate, minMag, maxMag)
        } else {
            viewModel.loadEarthquakes(minMag, maxMag, numDays)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_item -> {
                    val mode = QueryPreferences.getMode(this)
                    val startDate = QueryPreferences.getStartDate(this)
                    val endDate = QueryPreferences.getEndDate(this)
                    val minMag = QueryPreferences.getMinMag(this)
                    val maxMag = QueryPreferences.getMaxMag(this)
                    val numDays = QueryPreferences.getNumDays(this)
                    val dialog = EarthquakeOptionsDialog.newInstance(mode, startDate, endDate, minMag, maxMag, numDays)
                    dialog.show(supportFragmentManager, "options")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onOptionsSelected(mode: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int, numDays: Int) {
        QueryPreferences.setMode(this, mode)
        QueryPreferences.setStartDate(this, startDate)
        QueryPreferences.setEndDate(this, endDate)
        QueryPreferences.setMinMag(this, minMag)
        QueryPreferences.setMaxMag(this, maxMag)
        QueryPreferences.setNumDays(this, numDays)
        if (mode == QueryPreferences.MODE_RANGE) {
            viewModel.loadEarthquakes(startDate, endDate, minMag, maxMag)
        } else {
            viewModel.loadEarthquakes(minMag, maxMag, numDays)
        }
    }
}
