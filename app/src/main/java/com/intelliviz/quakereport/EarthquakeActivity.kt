/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intelliviz.quakereport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.earthquake_activity.*
import java.util.*

class EarthquakeActivity : AppCompatActivity() {

    private lateinit var viewModel: EarthquakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)
        var url: String = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2016-05-02&endtime=2016-05-03&format=geojson&minmag=4.5"
        //https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10

        // Create a fake list of earthquake locations.
        val earthquakes = ArrayList<Earthquake>()
//        earthquakes.add("San Francisco")
//        earthquakes.add("London")
//        earthquakes.add("Tokyo")
//        earthquakes.add("Mexico City")
//        earthquakes.add("Moscow")
//        earthquakes.add("Rio de Janeiro")
//        earthquakes.add("Paris")

        // Find a reference to the {@link ListView} in the layout
        //val earthquakeListView = findViewById<View>(R.id.earthquakeListView) as RecyclerView

        // Create a new {@link ArrayAdapter} of earthquakes
        val adapter = EarthquakeAdapter(this, earthquakes)

        //earthquakeListView.layoutManager = LinearLayoutManager(this)
        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        earthquakeListView.layoutManager = layoutManager
        //earthquakeListView.addItemDecoration(DividerItemDecoration(this, layoutManager.getOrientation()))
        earthquakeListView.adapter = adapter

        earthquakeListView.setOnClickListener {
            Toast.makeText(this, "HERE", Toast.LENGTH_LONG).show()
        }

        val earthquakeObserver = Observer<List<Earthquake>> { earthquake ->
            adapter.addAll(earthquakes)
        }

        viewModel = ViewModelProviders.of(this).get(EarthquakeViewModel::class.java)
        viewModel.earthquakes?.observe(this, earthquakeObserver)
        viewModel.loadEarthquakes(url)


        //GetEarthQuakeDataAsyncTask(adapter).execute(url)

    }

    companion object {

        val LOG_TAG = EarthquakeActivity::class.java.name
    }
}
