package com.intelliviz.quakereport

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.Earthquake

class EarthquakeRepository(var context: Context) : GetEarthQuakeDataAsyncTask.OnEarthquakeLoadListener {
    override fun onEarthquakeLoad(earthquakes: List<Earthquake>) {
       this._earthquakes.value = earthquakes
    }

    var _earthquakes = MutableLiveData<List<Earthquake>>()

    fun getEarthquakes(): LiveData<List<Earthquake>> {
        return AppDatabase.getAppDataBase(context)?.earthquakeDao()!!.getEarthquakes()
    }

    fun loadEarthQuakes(url: String) {
        GetEarthQuakeDataAsyncTask(this).execute(url)
    }
}