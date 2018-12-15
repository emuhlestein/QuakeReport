package com.intelliviz.quakereport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.intelliviz.quakereport.EarthquakeOptionsDialog.Companion.EXTRA_END_DATE
import com.intelliviz.quakereport.EarthquakeOptionsDialog.Companion.EXTRA_MAX_MAG
import com.intelliviz.quakereport.EarthquakeOptionsDialog.Companion.EXTRA_MIN_MAG
import com.intelliviz.quakereport.EarthquakeOptionsDialog.Companion.EXTRA_START_DATE
import kotlinx.android.synthetic.main.earthquake_activity.*
import java.util.*

class EarthquakeActivity : AppCompatActivity(), EarthquakeOptionsDialog.OnOptionsSelectedListener {


    private lateinit var viewModel: EarthquakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)
        val url: String = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2016-05-02&endtime=2016-05-03&format=geojson&minmag=4.5"
        //https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10

        val earthquakes = ArrayList<Earthquake>()
        val adapter = EarthquakeAdapter(this, earthquakes)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        earthquakeListView.layoutManager = layoutManager
        earthquakeListView.adapter = adapter

        earthquakeListView.setOnClickListener {
            Toast.makeText(this, "HERE", Toast.LENGTH_LONG).show()
        }

        val earthquakeObserver = Observer<List<Earthquake>> { earthquake ->
            val earthquakeData =  ArrayList<Earthquake>(earthquake)
            adapter.addAll(earthquakeData)
        }

        var endDate: String = QueryPreferences.getEndDate(this)
        var startDate: String = QueryPreferences.getStartDate(this)
        var minMag: Int = QueryPreferences.getMinMag(this)
        var maxMag: Int = QueryPreferences.getMaxMag(this)

        viewModel = ViewModelProviders.of(this).get(EarthquakeViewModel::class.java)
        viewModel.getEarthquakes().observe(this, earthquakeObserver)
        //viewModel.loadEarthquakes(url)

        viewModel.loadEarthquakes(endDate, startDate, minMag, maxMag)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.options_item -> {
                //val newFragment = DatePickerFragment()
                //newFragment.show(supportFragmentManager, "date picker")
                var startDate = QueryPreferences.getStartDate(this)
                var endDate = QueryPreferences.getEndDate(this)
                val minMag = QueryPreferences.getMinMag(this)
                val maxMag = QueryPreferences.getMaxMag(this)
                var dialog: EarthquakeOptionsDialog = EarthquakeOptionsDialog.newInstance(0, startDate, endDate, minMag, maxMag)
                dialog.show(supportFragmentManager, "options")
//                intent = Intent(this, EarthquakeOptionsActivity::class.java)
//                startActivityForResult(intent, REQUEST_CODE)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val endDate = data?.getStringExtra(EXTRA_END_DATE)
        val startDate = data?.getStringExtra(EXTRA_START_DATE)
        val minMag = data?.getIntExtra(EXTRA_MIN_MAG, DEFAULT_MAGNITUDE)
        var maxMag = data?.getIntExtra(EXTRA_MAX_MAG, DEFAULT_MAGNITUDE)
        val url = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=$startDate&endtime=$endDate&format=geojson&minmag=$minMag"
        viewModel.loadEarthquakes(url)
    }

//    override fun onDateSelected(day: String, month: String, year: String, id: Int) {
//        val dateStart = year + "-" + month + "-" + day
//        val dateEnd = year + "-" + month + "-" + (day+1)
//        Toast.makeText(this, "selected date is $dateStart", Toast.LENGTH_SHORT).show()
//        val url = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=$dateStart&endtime=$dateEnd&format=geojson&minmag=4.5"
//        viewModel.loadEarthquakes(url)
//        Log.d("TAG", dateStart)
//    }

    override fun onOptionsSelected(startDate: String, endDate: String, minMag: Int, maxMag: Int) {
        QueryPreferences.setStartDate(this, startDate)
        QueryPreferences.setEndDate(this, endDate)
        QueryPreferences.setMinMag(this, minMag)
        QueryPreferences.setMaxMag(this, maxMag)
        viewModel.loadEarthquakes(endDate, startDate, minMag, maxMag)
    }

    companion object {
        val LOG_TAG = EarthquakeActivity::class.java.name
        val REQUEST_CODE = 1
        val DEFAULT_MAGNITUDE = 0
    }
}
