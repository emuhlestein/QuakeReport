package com.intelliviz.quakereport

import android.arch.lifecycle.MutableLiveData

class EarthquakeRepository : GetEarthQuakeDataAsyncTask.OnEarthquakeLoadListener {
    override fun onEarthquakeLoad(earthquakes: List<Earthquake>) {
       this.earthquakes.value = earthquakes
    }

    var earthquakes = MutableLiveData<List<Earthquake>>()

//    fun getEarthquakes(): MutableLiveData<List<Earthquake>> {
//        return earthquakes
//    }

    fun loadEarthQuakes(url: String) {
        GetEarthQuakeDataAsyncTask(this).execute(url)
    }
}