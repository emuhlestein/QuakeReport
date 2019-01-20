package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import com.intelliviz.quakereport.QueryUtils.EXTRA_END_DATE
import com.intelliviz.quakereport.QueryUtils.EXTRA_MAX_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_MIN_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_START_DATE
import com.intelliviz.quakereport.db.Earthquake


class EarthquakeRangeViewModel(application: Application, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null
    init {
        repo = EarthquakeRepository(application)
        subscriberToDatabaseChanges()
        requestEarthquakes(endDate, startDate, minMag, maxMag)
    }

    private var earthquakes: LiveData<List<Earthquake>>? = null

    private fun subscriberToDatabaseChanges() {
        earthquakes = repo?.getEarthquakes()
    }

    private fun requestEarthquakes(endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        loadEarthquakes(endDate, startDate, minMag, maxMag)
    }

    fun getEarthquakes() = earthquakes

    fun loadEarthquakes(endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        val intent = createIntent(endDate, startDate, minMag, maxMag)
        getApplication<Application>().startService(intent)
    }

    fun loadEarthquakes(numDays: Int?, minMag: Int?, maxMag: Int?) {
        val intent = createIntent(numDays, minMag, maxMag)
        getApplication<Application>().startService(intent)
    }

    private fun createIntent(numDays: Int?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(getApplication(), EarthquakeService::class.java)
        val endDate = QueryPreferences.getCurrentDate()
        val startDate = QueryPreferences.getCurrentDate(numDays!!)
        intent.putExtra(QueryUtils.EXTRA_START_DATE, startDate)
        intent.putExtra(QueryUtils.EXTRA_END_DATE, endDate)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }

    private fun createIntent(endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(getApplication(), EarthquakeService::class.java)
        intent.putExtra(EXTRA_START_DATE, startDate)
        intent.putExtra(EXTRA_END_DATE, endDate)
        intent.putExtra(EXTRA_MIN_MAG, minMag)
        intent.putExtra(EXTRA_MAX_MAG, maxMag)
        return intent
    }

    class Factory(private val mApplication: Application,
                  private val endDate: String?, private val startDate: String?,
                  private val minMag: Int?,
                  private val maxMag: Int?) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeRangeViewModel(mApplication, endDate, startDate,  minMag, maxMag) as T
        }
    }
}