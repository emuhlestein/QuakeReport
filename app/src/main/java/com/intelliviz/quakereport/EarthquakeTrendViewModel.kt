package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.*
import android.content.Intent
import com.intelliviz.quakereport.db.EarthquakeEntity

class EarthquakeTrendViewModel(application: Application, year: Int?, minMag: Int?, maxMag: Int?): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null
    val earthquakes = MediatorLiveData<List<Earthquake>>()
    val dbEarthquakes: LiveData<List<EarthquakeEntity>>?
    init {
        repo = EarthquakeRepository(application)
        dbEarthquakes = repo?.getEarthquakes()

        earthquakes.addSource(dbEarthquakes!!) {value ->
            value?.let{ earthquakes.value = sortQuakes(it)}
        }
    }

    fun loadEarthquakes(year: Int?, minMag: Int?, maxMag: Int?) {
        repo?.loadEarthquakes(getApplication(), year, minMag, maxMag)
    }

    private fun createIntent(year: Int?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(getApplication(), EarthquakeService::class.java)
        intent.action = ACTION_EARTHQUAKE_TREND
        intent.putExtra(QueryUtils.EXTRA_YEAR, year)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }

    private fun sortQuakes(list: List<EarthquakeEntity>): List<Earthquake> {
        val earthquakes = ArrayList<Earthquake>()
        return earthquakes
    }

    class Factory(private val mApplication: Application,
                  private val year: Int?,
                  private val minMag: Int?,
                  private val maxMag: Int?) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeTrendViewModel(mApplication, year,  minMag, maxMag) as T
        }
    }
}