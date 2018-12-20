package com.intelliviz.quakereport

import android.arch.lifecycle.LiveData
import android.content.Context
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
}