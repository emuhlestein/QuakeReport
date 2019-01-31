package com.intelliviz.quakereport.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.intelliviz.quakereport.*
import com.intelliviz.quakereport.QueryPreferences.MODE_RANGE
import kotlinx.android.synthetic.main.activity_earthquake.*
import java.util.*

class EarthquakeMainActivity : AppCompatActivity(),
        EarthquakeOptionsDialog.OnOptionsSelectedListener {
    private lateinit var viewModel: EarthquakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earthquake)

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

            //var sortedList = earthquakeData.sortedWith(compareBy({e.}))
            adapter.addAll(earthquakeData)
        }

        val mode: Int = QueryPreferences.getMode(this)
        val sort: Int = QueryPreferences.getSort(this)
        val endDate: String = QueryPreferences.getEndDate(this)
        val startDate: String = QueryPreferences.getStartDate(this)
        val minMag: Int = QueryPreferences.getMinMag(this)
        val maxMag: Int = QueryPreferences.getMaxMag(this)
        val numDays: Int = QueryPreferences.getNumDays(this)

        val factory: EarthquakeViewModel.Factory = EarthquakeViewModel.Factory(application, sort)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeViewModel::class.java)
        viewModel.init(mode, sort, startDate, endDate, minMag, maxMag, numDays)
        viewModel.earthquakes?.observe(this, earthquakeObserver)

        if(mode == MODE_RANGE) {
            viewModel.loadEarthquakes(mode, sort, endDate, startDate, minMag, maxMag)
        } else {
            viewModel.loadEarthquakes(mode, sort, minMag, maxMag, numDays)
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
                    val sort = QueryPreferences.getSort(this)
                    val startDate = QueryPreferences.getStartDate(this)
                    val endDate = QueryPreferences.getEndDate(this)
                    val minMag = QueryPreferences.getMinMag(this)
                    val maxMag = QueryPreferences.getMaxMag(this)
                    val numDays = QueryPreferences.getNumDays(this)
                    val dialog = EarthquakeOptionsDialog.newInstance(mode, sort, startDate, endDate, minMag, maxMag, numDays)
                    dialog.show(supportFragmentManager, "options")
            }
            R.id.trends_item -> {
                val mode = QueryPreferences.getMode(this)
                val year = QueryPreferences.getYear(this)
                val minMag = QueryPreferences.getMinMag(this)
                val maxMag = QueryPreferences.getMaxMag(this)
                val intent = Intent(application, EarthquakeTrendsActivity::class.java)
                intent.action = ACTION_EARTHQUAKE_TREND
                intent.putExtra(QueryUtils.EXTRA_YEAR, year)
                intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
                intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onOptionsSelected(mode: Int, sort: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int, numDays: Int) {
        if (mode == QueryPreferences.MODE_RANGE) {
            viewModel.loadEarthquakes(mode, sort, startDate, endDate, minMag, maxMag)
        } else {
            viewModel.loadEarthquakes(mode, sort, minMag, maxMag, numDays)
        }
    }
}
