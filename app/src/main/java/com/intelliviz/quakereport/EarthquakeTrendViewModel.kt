package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.intelliviz.quakereport.db.DownloadStatusEntity
import com.intelliviz.quakereport.db.EarthquakeInfoEntity
import com.intelliviz.quakereport.graphview.PointValue

class EarthquakeTrendViewModel(application: Application): AndroidViewModel(application) {
    private val repo = EarthquakeRepository(application)

    val status = MediatorLiveData<DownloadStatus>()
    val earthquakeInfo = MediatorLiveData<EarthquakeTrendViewData>()
    init {

        val dbEarthquakeInfo = repo.getEarthquakeInfo()
        earthquakeInfo.addSource(dbEarthquakeInfo) {value ->
            value?.let{ earthquakeInfo.value = mapQuakes(it)}
        }

        val dbStatus = repo.getDownloadStatus()
        status.addSource(dbStatus) {value ->
            value?.let { status.value = mapper(it) }
        }
    }

    fun loadEarthquakes(year: Int?, minMag: Int?, maxMag: Int?) {
        repo.loadEarthquakes(getApplication(), year, minMag, maxMag)
    }

    private fun mapQuakes(list: List<EarthquakeInfoEntity>): EarthquakeTrendViewData {
        val earthquakes = ArrayList<EarthquakeInfo>()
        val pointValues = ArrayList<PointValue>()
        val colors = LinkedHashMap<Int, Int>()

        val set = HashSet<Int>()
        list.forEach{
            set.add(it.magnitude)
        }

        val sortedSet = set.toSortedSet()
        sortedSet.forEach{
            colors[it] = QueryUtils.getMagnitudeColor(getApplication(), it.toFloat())
        }

        list.forEach{
            val point = PointValue(it.year.toFloat(), it.count.toFloat(), it.magnitude)
            pointValues.add(point)
            val earthquakeInfo = EarthquakeInfo(it.year, it.magnitude, it.count)
            earthquakes.add(earthquakeInfo)
        }

        return EarthquakeTrendViewData(pointValues, colors)
    }

    private fun mapper(status: DownloadStatusEntity): DownloadStatus {
        return DownloadStatus(status.status, status.progress)
    }

    class Factory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeTrendViewModel(mApplication) as T
        }
    }
}