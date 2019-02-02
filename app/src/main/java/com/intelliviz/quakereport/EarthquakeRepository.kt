package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import com.intelliviz.quakereport.db.*

class EarthquakeRepository(private val context: Context) {
    private val earthquakeDao: EarthquakeDao
    private val earthquakeInfoDao: EarthquakeInfoDao


    init {
        val db = AppDatabase.getAppDataBase(context)
        earthquakeDao = db!!.earthquakeDao()
        earthquakeInfoDao = db!!.earthquakeInfoDao()
    }

    fun getEarthquakes(): LiveData<List<EarthquakeEntity>> {
        return earthquakeDao.getEarthquakes()
    }

    fun getEarthquakeInfo(): LiveData<List<EarthquakeInfoEntity>> {
        return earthquakeInfoDao.getEarthquakeInfo()
    }

    fun loadEarthquakes(application: Application, mode: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?) {
        if(needToDownloadRange(mode, startDate, endDate, minMag, maxMag) ) {
            val intent = createIntent(application, mode, startDate, endDate, minMag, maxMag)
            application.startService(intent)
        }
    }

    fun loadEarthquakes(application: Application, mode: Int, minMag: Int?, maxMag: Int?, numDays: Int?) {
        if(needToDownloadRecent(mode, minMag, maxMag, numDays)) {
            val intent = createIntent(application, mode, minMag, maxMag, numDays)
            application.startService(intent)
        }
    }

    fun loadEarthquakes(application: Application, year: Int?, minMag: Int?, maxMag: Int?) {
        //if(needToDownloadRecent(year, minMag, maxMag)) {
            val intent = createIntent(application, year, minMag, maxMag)
            application.startService(intent)
        //}
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

    private fun createIntent(application: Application, mode: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(application, EarthquakeService::class.java)
        intent.action = ACTION_EARTHQUAKE_RANGE
        intent.putExtra(QueryUtils.EXTRA_MODE, mode)
        intent.putExtra(QueryUtils.EXTRA_START_DATE, startDate)
        intent.putExtra(QueryUtils.EXTRA_END_DATE, endDate)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }

    private fun createIntent(application: Application, year: Int?, minMag: Int?, maxMag: Int?): Intent {
        val intent = Intent(application, EarthquakeService::class.java)
        intent.action = ACTION_EARTHQUAKE_TREND
        intent.putExtra(QueryUtils.EXTRA_YEAR, year)
        intent.putExtra(QueryUtils.EXTRA_MIN_MAG, minMag)
        intent.putExtra(QueryUtils.EXTRA_MAX_MAG, maxMag)
        return intent
    }

    private fun needToDownloadRange(mode: Int, startDate: String?, endDate: String?, minMag: Int?, maxMag: Int?): Boolean {
        val currentMode = QueryPreferences.getMode(context)
        val currentMinMag = QueryPreferences.getMinMag(context)
        val currentMaxMag = QueryPreferences.getMaxMag(context)
        val currentEndDate = QueryPreferences.getEndDate(context)
        val currentStartDate = QueryPreferences.getStartDate(context)

        return if(mode != currentMode ||
                endDate != currentEndDate || startDate != currentStartDate ||
                minMag != currentMinMag || maxMag != currentMaxMag) {
            QueryPreferences.setMode(context, mode)
            QueryPreferences.setMinMag(context, minMag!!)
            QueryPreferences.setMaxMag(context, maxMag!!)
            QueryPreferences.setStartDate(context, startDate!!)
            QueryPreferences.setEndDate(context, endDate!!)
            true
        } else {
            false
        }
    }

    private fun needToDownloadRecent(mode: Int, minMag: Int?, maxMag: Int?, numDays: Int?): Boolean {
        val currentMode = QueryPreferences.getMode(context)
        val currentMinMag = QueryPreferences.getMinMag(context)
        val currentMaxMag = QueryPreferences.getMaxMag(context)
        val currentNumDays = QueryPreferences.getNumDays(context)

        return if(mode != currentMode || numDays != currentNumDays ||
                minMag != currentMinMag || maxMag != currentMaxMag) {
            QueryPreferences.setMode(context, mode)
            QueryPreferences.setMinMag(context, minMag!!)
            QueryPreferences.setMaxMag(context, maxMag!!)
            QueryPreferences.setNumDays(context, numDays!!)
            true
        } else {
            false
        }
    }

    private fun needToDownloadRecent(year: Int?, minMag: Int?, maxMag: Int?): Boolean {
        val currentYear = QueryPreferences.getYear(context)
        val currentMinMag = QueryPreferences.getMinMag(context)
        val currentMaxMag = QueryPreferences.getMaxMag(context)

        return if(year != currentYear ||
                minMag != currentMinMag || maxMag != currentMaxMag) {
            QueryPreferences.setYear(context, year!!)
            QueryPreferences.setMinMag(context, minMag!!)
            QueryPreferences.setMaxMag(context, maxMag!!)
            true
        } else {
            false
        }
    }
}