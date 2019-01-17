package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import com.intelliviz.quakereport.db.Earthquake

class EarthquakeTrendViewModel(application: Application, year: Int?, minMag: Int?, maxMag: Int?): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null
    init {
        repo = EarthquakeRepository(application)
        subscriberToDatabaseChanges()
        requestEarthquakes(year, minMag, maxMag)
    }

    private var earthquakes: LiveData<List<Earthquake>>? = null

    private fun subscriberToDatabaseChanges() {
        earthquakes = repo?.getEarthquakes()
    }

    fun getEarthquakes() = earthquakes

    private fun requestEarthquakes(year: Int?, minMag: Int?, maxMag: Int?) {
        loadEarthquakes(year, minMag, maxMag)
    }

    fun loadEarthquakes(year: Int?, minMag: Int?, maxMag: Int?) {
        val intent = createIntent(year, minMag, maxMag)
        getApplication<Application>().startService(intent)
    }

    private fun createIntent(year: Int?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(getApplication(), EarthquakeService::class.java)
        intent.action = ACTION_EARTHQUAKE_TREND
        intent.putExtra(QueryUtils.EXTRA_YEAR, year)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
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