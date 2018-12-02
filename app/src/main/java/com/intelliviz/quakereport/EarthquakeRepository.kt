package com.intelliviz.quakereport

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

class EarthquakeRepository : GetEarthQuakeDataAsyncTask.OnEarthquakeLoadListener {
    override fun onEarthquakeLoad(earthquakes: List<Earthquake>) {
       this._earthquakes.value = earthquakes
    }

    var _earthquakes = MutableLiveData<List<Earthquake>>()

    fun getEarthquakes(): LiveData<List<Earthquake>> {
        return _earthquakes
    }

    fun loadEarthQuakes(url: String) {
        GetEarthQuakeDataAsyncTask(this).execute(url)
    }
}