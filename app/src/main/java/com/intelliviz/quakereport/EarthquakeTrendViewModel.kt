package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.*
import android.content.Intent
import android.graphics.Color
import com.intelliviz.quakereport.db.EarthquakeInfoEntity
import com.intelliviz.quakereport.graphview.PointValue

class EarthquakeTrendViewModel(application: Application): AndroidViewModel(application) {
    private var repo: EarthquakeRepository? = null
    val earthquakeInfo = MediatorLiveData<EarthquakeTrendViewData>()
    val dbEarthquakeInfo: LiveData<List<EarthquakeInfoEntity>>?
    init {
        repo = EarthquakeRepository(application)
        dbEarthquakeInfo = repo?.getEarthquakeInfo()

        earthquakeInfo.addSource(dbEarthquakeInfo!!) {value ->
            value?.let{ earthquakeInfo.value = mapQuakes(it)}
        }
    }

    fun loadEarthquakes(year: Int?, minMag: Int?, maxMag: Int?) {
        repo?.loadEarthquakes(getApplication(), year, minMag, maxMag)
    }

    fun init(year: Int, minMag: Int?, maxMag: Int?) {
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

    private fun mapQuakes(list: List<EarthquakeInfoEntity>): EarthquakeTrendViewData {
        val earthquakes = ArrayList<EarthquakeInfo>()
        val pointValues = ArrayList<PointValue>()
        val colors = HashMap<Int, Int>()
        list.forEach{
            val point = PointValue(it.year.toFloat(),  it.count.toFloat(), it.magnitude)
            var color = Color.BLUE
            pointValues.add(point)

            var earthquakeInfo = EarthquakeInfo(it.year, it.magnitude, it.count)
            earthquakes.add(earthquakeInfo)
        }

        colors[6] = Color.GREEN
        colors[7] = Color.RED
        colors[8] = Color.MAGENTA

        var earthquakeInfo = EarthquakeTrendViewData(pointValues, colors)
        return earthquakeInfo
    }

    class Factory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeTrendViewModel(mApplication) as T
        }
    }
}