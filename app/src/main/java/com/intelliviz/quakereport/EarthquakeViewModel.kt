package com.intelliviz.quakereport

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel


class EarthquakeViewModel: ViewModel() {
    val repo = EarthquakeRepository()
    var earthquakes: LiveData<List<Earthquake>>? = null

    init {
        earthquakes = repo.earthquakes
    }

    fun loadEarthquakes(url: String) {
        repo.loadEarthQuakes(url)
    }
}