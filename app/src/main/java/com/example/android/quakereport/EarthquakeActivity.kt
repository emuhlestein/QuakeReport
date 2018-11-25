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
package com.example.android.quakereport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.*

class EarthquakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)
        var url: String = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2016-05-02&endtime=2016-05-03&format=geojson&minmag=4.5"

        // Create a fake list of earthquake locations.
        val earthquakes = ArrayList<String>()
        earthquakes.add("San Francisco")
        earthquakes.add("London")
        earthquakes.add("Tokyo")
        earthquakes.add("Mexico City")
        earthquakes.add("Moscow")
        earthquakes.add("Rio de Janeiro")
        earthquakes.add("Paris")

        // Find a reference to the {@link ListView} in the layout
        val earthquakeListView = findViewById<View>(R.id.list) as ListView

        // Create a new {@link ArrayAdapter} of earthquakes
        val adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1, earthquakes)

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.adapter = adapter

        GetEarthQuakeDataAsyncTask(adapter).execute(url)
    }

    companion object {

        val LOG_TAG = EarthquakeActivity::class.java.name
    }
}
