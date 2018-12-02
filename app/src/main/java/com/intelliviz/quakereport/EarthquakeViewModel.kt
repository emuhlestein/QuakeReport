package com.intelliviz.quakereport

import android.arch.lifecycle.ViewModel


class EarthquakeViewModel: ViewModel() {
    val repo = EarthquakeRepository()
    //var earthquakes: LiveData<List<Earthquake>>? = MutableLiveData()

    fun getEarthquakes() = repo.getEarthquakes()

    fun loadEarthquakes(url: String) {
        repo.loadEarthQuakes(url)
    }
}