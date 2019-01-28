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

    private var sort = 0

    fun init(mode: Int, sort: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?, numDays: Int) {
        this.sort = sort
        if(mode == MODE_RECENT) {
            repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, startDate, endDate, minMag, maxMag)
        }

        earthquakes.addSource(dbEarthquakes!!) {value ->
            value?.let{ earthquakes.value = sortQuakes(it, sort)}
        }
    }

    private fun sortQuakes(list: List<EarthquakeEntity>, sortBy: Int): List<Earthquake> {
        val earthquakes = ArrayList<Earthquake>()
        list.forEach{ e -> earthquakes.add(mapper(e))}

        if(sortBy == QueryPreferences.SORT_MAG) {
            return earthquakes.sortedWith(compareByDescending({ it.magnitude }))
        } else {
            return earthquakes
        }
    }

    private fun sortQuakes(sort: Int) = dbEarthquakes?.value?.let {
        val earthquakes = ArrayList<Earthquake>()
        it.forEach{ e -> earthquakes.add(mapper(e))}

        if(sort == QueryPreferences.SORT_MAG) {
            this.earthquakes.value = earthquakes.sortedWith(compareByDescending({ it.magnitude }))
        } else {
            this.earthquakes.value = earthquakes
        }
    }

    private fun mapper(ee: EarthquakeEntity): Earthquake {
        val displayDate = QueryUtils.getDateToDisplay(ee.date)
        val displayTime = QueryUtils.getTimeToDisplay(ee.date)
        return Earthquake(ee.magnitude, ee.distance, ee.city, displayDate, displayTime, ee.url)
    }

    fun loadEarthquakes(mode: Int, sort: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?) {
        if(this.sort != sort) {
            this.sort = sort
            QueryPreferences.setSort(getApplication(), sort)
            sortQuakes(sort)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, startDate, endDate, minMag, maxMag)
        }
    }

    fun loadEarthquakes(mode: Int, sort: Int, minMag: Int?, maxMag: Int?, numDays: Int?) {
        if(this.sort != sort) {
            this.sort = sort
            QueryPreferences.setSort(getApplication(), sort)
            sortQuakes(sort)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
        }
    }

    class Factory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeViewModel(mApplication) as T
        }
    }
}