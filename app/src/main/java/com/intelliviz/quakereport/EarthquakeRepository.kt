package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.Earthquake
import com.intelliviz.quakereport.db.EarthquakeDao

class EarthquakeRepository(context: Context) {
    private val earthquakeDao: EarthquakeDao

    init {
        val db = AppDatabase.getAppDataBase(context)
        earthquakeDao = db?.earthquakeDao()!!
    }

    fun getEarthquakes(): LiveData<List<Earthquake>> {
        return earthquakeDao.getEarthquakes()
    }

    fun loadEarthquakes(application: Application, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        val intent = createIntent(application, endDate, startDate, minMag, maxMag)
        application.startService(intent)
    }

    fun loadEarthquakes(application: Application, minMag: Int?, maxMag: Int?, numDays: Int?) {
        val intent = createIntent(application, numDays, minMag, maxMag)
        application.startService(intent)
    }

    private fun createIntent(application: Application, numDays: Int?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(application, EarthquakeService::class.java)
        val endDate = QueryPreferences.getCurrentDate()
        val startDate = QueryPreferences.getCurrentDate(numDays!!)
        intent.putExtra(QueryUtils.EXTRA_START_DATE, startDate)
        intent.putExtra(QueryUtils.EXTRA_END_DATE, endDate)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }

    private fun createIntent(application: Application, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(application, EarthquakeService::class.java)
        intent.putExtra(QueryUtils.EXTRA_START_DATE, startDate)
        intent.putExtra(QueryUtils.EXTRA_END_DATE, endDate)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }
}