package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.*
import com.intelliviz.quakereport.QueryPreferences.MODE_RECENT
import com.intelliviz.quakereport.db.DownloadStatusEntity
import com.intelliviz.quakereport.db.EarthquakeEntity


class EarthquakeViewModel(application: Application, sort: Int): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null

    val earthquakes = MediatorLiveData<List<Earthquake>>()
    val dbEarthquakes: LiveData<List<EarthquakeEntity>>?
    init {
        repo = EarthquakeRepository(application)
        dbEarthquakes = repo?.getEarthquakes()

        earthquakes.addSource(dbEarthquakes!!) {value ->
            value?.let{ earthquakes.value = sortQuakes(it, sort)}
        }
    }

    private var sort = 0

    fun init(mode: Int, sort: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?, numDays: Int) {
        this.sort = sort
        if(mode == MODE_RECENT) {
            repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, startDate, endDate, minMag, maxMag)
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
        if(needToSort(sort, startDate, endDate, minMag, maxMag)) {
            QueryPreferences.setSort(getApplication(), sort)
            sortQuakes(sort)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, startDate, endDate, minMag, maxMag)
        }
    }

    fun loadEarthquakes(mode: Int, sort: Int, minMag: Int, maxMag: Int, numDays: Int) {
        if(needToSort(sort, minMag, maxMag, numDays)) {
            QueryPreferences.setSort(getApplication(), sort)
            sortQuakes(sort)
        } else {
            repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
        }
    }

    fun needToSort(sort: Int, minMag: Int, maxMag: Int, numDays: Int): Boolean {
        val currentSort = QueryPreferences.getSort(getApplication())
        val currentMinMag = QueryPreferences.getMinMag(getApplication())
        val currentMaxMag = QueryPreferences.getMaxMag(getApplication())
        val currentNumDays = QueryPreferences.getNumDays(getApplication())
        if(currentMinMag == minMag && currentMaxMag == maxMag &&
           currentNumDays == numDays && currentSort != sort ) {
            QueryPreferences.setSort(getApplication(), sort)
            return true
        } else {
            return false
        }
    }

    fun needToSort(sort: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?): Boolean {
        val currentSort = QueryPreferences.getSort(getApplication())
        val currentMinMag = QueryPreferences.getMinMag(getApplication())
        val currentMaxMag = QueryPreferences.getMaxMag(getApplication())
        val currentEndDate = QueryPreferences.getEndDate(getApplication())
        val currentStartDate = QueryPreferences.getStartDate(getApplication())
        if(currentMinMag == minMag && currentMaxMag == maxMag &&
           currentEndDate == endDate && currentStartDate == startDate &&
           currentSort != sort ) {
            QueryPreferences.setSort(getApplication(), sort)
            return true
        } else {
            return false
        }
    }

    class Factory(private val mApplication: Application, private val sort: Int) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeViewModel(mApplication, sort) as T
        }
    }
}