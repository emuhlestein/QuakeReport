package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.intelliviz.quakereport.QueryPreferences.MODE_RECENT
import com.intelliviz.quakereport.db.EarthquakeEntity


class EarthquakeViewModel(application: Application): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null
    init {
        repo = EarthquakeRepository(application)
    }

    private val dbEarthquakes = repo?.getEarthquakes()

    val earthquakes = MediatorLiveData<List<Earthquake>>()

    fun init(mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?, numDays: Int) {
        if(mode == MODE_RECENT) {
            repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, endDate, startDate, minMag, maxMag)
        }

        earthquakes.addSource(dbEarthquakes!!) {value ->
            value?.let{ earthquakes.value = sortQuakes(it, 1)}
        }
    }

    private fun sortQuakes(list: List<EarthquakeEntity>, sortBy: Int): List<Earthquake> {
        var earthquakes = ArrayList<Earthquake>()
        list.forEach{ e -> earthquakes.add(mapper(e))}
        return earthquakes.sortedWith(compareByDescending({ it.magnitude }))
    }

    private fun mapper(ee: EarthquakeEntity): Earthquake {
        return Earthquake(ee.magnitude, ee.distance, ee.city, ee.date, ee.time, ee.url)
    }

    fun loadEarthquakes(mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        repo?.loadEarthquakes(getApplication(), mode, endDate, startDate, minMag, maxMag)
    }

    fun loadEarthquakes(mode: Int, minMag: Int?, maxMag: Int?, numDays: Int?) {
        repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
    }

    class Factory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeViewModel(mApplication) as T
        }
    }
}