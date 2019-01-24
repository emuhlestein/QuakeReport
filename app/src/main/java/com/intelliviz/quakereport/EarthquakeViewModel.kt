package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.intelliviz.quakereport.db.Earthquake


class EarthquakeViewModel(application: Application, mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null
    init {
        repo = EarthquakeRepository(application)
        subscriberToDatabaseChanges()
        requestEarthquakes(mode, endDate, startDate, minMag, maxMag)
    }

    private var earthquakes: LiveData<List<Earthquake>>? = null

    private fun subscriberToDatabaseChanges() {
        earthquakes = repo?.getEarthquakes()
    }

    private fun requestEarthquakes(mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        loadEarthquakes(mode, endDate, startDate, minMag, maxMag)
    }

    fun getEarthquakes() = earthquakes

    fun loadEarthquakes(mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        repo?.loadEarthquakes(getApplication(), mode, endDate, startDate, minMag, maxMag)
    }

    fun loadEarthquakes(mode: Int, minMag: Int?, maxMag: Int?, numDays: Int?) {
        repo?.loadEarthquakes(getApplication(), mode, minMag, maxMag, numDays)
    }

    class Factory(private val mApplication: Application,
                  private val mode: Int,
                  private val endDate: String?,
                  private val startDate: String?,
                  private val minMag: Int?,
                  private val maxMag: Int?) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeViewModel(mApplication, mode, endDate, startDate,  minMag, maxMag) as T
        }
    }
}