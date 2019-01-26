package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.EarthquakeDao
import com.intelliviz.quakereport.db.EarthquakeEntity

class EarthquakeRepository(context: Context) {
    private val earthquakeDao: EarthquakeDao

    init {
        val db = AppDatabase.getAppDataBase(context)
        earthquakeDao = db?.earthquakeDao()!!
    }

    fun getEarthquakes(): LiveData<List<EarthquakeEntity>> {
        return earthquakeDao.getEarthquakes()
    }

    fun getEarthquakes(sortField: String): LiveData<List<EarthquakeEntity>> {
        val listLD: LiveData<List<EarthquakeEntity>> = earthquakeDao.getEarthquakes()
        val list: List<EarthquakeEntity>? = listLD.value
        return earthquakeDao.getEarthquakes()
    }

    fun loadEarthquakes(application: Application, mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?) {
        val intent = createIntent(application, mode, endDate, startDate, minMag, maxMag)
        application.startService(intent)
    }

    fun loadEarthquakes(application: Application, mode: Int, minMag: Int?, maxMag: Int?, numDays: Int?) {
        val intent = createIntent(application, mode, minMag, maxMag, numDays)
        application.startService(intent)
    }

    private fun createIntent(application: Application, mode: Int, minMag: Int?, maxMag: Int?, numDays: Int?): Intent {
        val intent = Intent(application, EarthquakeService::class.java)
        intent.action = ACTION_EARTHQUAKE_RECENT
        val endDate = QueryUtils.getCurrentDate()
        val startDate = QueryUtils.getCurrentDate(numDays!!)
        intent.putExtra(QueryUtils.EXTRA_MODE, mode)
        intent.putExtra(QueryUtils.EXTRA_NUM_DAYS, numDays)
        intent.putExtra(QueryUtils.EXTRA_START_DATE, startDate)
        intent.putExtra(QueryUtils.EXTRA_END_DATE, endDate)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }

    private fun createIntent(application: Application, mode: Int, endDate: String?, startDate: String?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(application, EarthquakeService::class.java)
        intent.action = ACTION_EARTHQUAKE_RANGE
        intent.putExtra(QueryUtils.EXTRA_MODE, mode)
        intent.putExtra(QueryUtils.EXTRA_START_DATE, startDate)
        intent.putExtra(QueryUtils.EXTRA_END_DATE, endDate)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }
}