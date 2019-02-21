package com.intelliviz.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.graphics.Color
import com.intelliviz.quakereport.db.DownloadStatusEntity
import com.intelliviz.quakereport.db.EarthquakeInfoEntity
import com.intelliviz.quakereport.graphview.PointValue

class EarthquakeTrendViewModel(application: Application): AndroidViewModel(application) {
    private val repo = EarthquakeRepository(application)

    val status = MediatorLiveData<DownloadStatus>()
    //val dbStatus: LiveData<DownloadStatusEntity>
    val earthquakeInfo = MediatorLiveData<EarthquakeTrendViewData>()
    //val dbEarthquakeInfo: LiveData<List<EarthquakeInfoEntity>>?
    init {

        val dbEarthquakeInfo = repo.getEarthquakeInfo()
        earthquakeInfo.addSource(dbEarthquakeInfo) {value ->
            value?.let{ earthquakeInfo.value = mapQuakes(it)}
        }

//        val dbStatus = repo.getDownloadStatus()
//        status = Transformations.map(dbStatus) {  data ->
//            mapper(data)
//        }
        val dbStatus = repo?.getDownloadStatus()
        status.addSource(dbStatus!!) {value ->
            value?.let { status.value = mapper(it) }
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
        val colors = LinkedHashMap<Int, Int>()

        val set = HashSet<Int>()
        list.forEach{
            set.add(it.magnitude)
        }

        var sortedSet = set.toSortedSet()
        sortedSet.forEach{
            colors.put(it, (QueryUtils.getMagnitudeColor(getApplication(), it.toFloat())))
        }

        list.forEach{
            val point = PointValue(it.year.toFloat(), it.count.toFloat(), it.magnitude)

            //colors.put(it.magnitude, color)
            pointValues.add(point)

            val earthquakeInfo = EarthquakeInfo(it.year, it.magnitude, it.count)
            earthquakes.add(earthquakeInfo)
        }

        return EarthquakeTrendViewData(pointValues, colors)
    }


    private fun mapper(status: DownloadStatusEntity): DownloadStatus {
        return DownloadStatus(status.status, status.progress)
    }

    private fun getColor(index: Int): Int {
        return when(index) {
            0 -> Color.GREEN
            1 -> Color.GREEN
            2 -> Color.GREEN
            3 -> Color.GREEN
            4 -> Color.GREEN
            5 -> Color.GREEN
            6 -> Color.GREEN
            7 -> Color.RED
            8 -> Color.BLUE
            9 -> Color.MAGENTA
            10 -> Color.BLACK
            11 -> Color.BLACK
            12 -> Color.BLACK
            else -> Color.GREEN
        }
    }

    class Factory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EarthquakeTrendViewModel(mApplication) as T
        }
    }
}