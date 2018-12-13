package com.intelliviz.quakereport

import android.arch.lifecycle.ViewModel


class EarthquakeViewModel: ViewModel() {
    val repo = EarthquakeRepository()
    //var earthquakes: LiveData<List<Earthquake>>? = MutableLiveData()

    fun getEarthquakes() = repo.getEarthquakes()

    fun loadEarthquakes(url: String) {
        repo.loadEarthQuakes(url)
    }

    fun loadEarthquakes(endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {

        var baseURL: String = "https://earthquake.usgs.gov/fdsnws/event/1/query?"
        var url: String = baseURL + "format=geojson"
        if(endDate != null && !endDate?.isEmpty()) {
            url = url + "&endtime=" + endDate
        }
        if(startDate != null && !startDate.isEmpty()) {
            url = url + "&starttime=" + startDate
        }
        if(minMag != null) {
            url = url + "&minmag=" + minMag
        }
        repo.loadEarthQuakes(url)
        //val url: String = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2016-05-02&endtime=2016-05-03&format=geojson&minmag=4.5"
    }
}