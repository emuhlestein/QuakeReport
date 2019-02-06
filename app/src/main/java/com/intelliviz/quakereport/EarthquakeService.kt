package com.intelliviz.quakereport

import android.app.IntentService
import android.content.Intent
import com.intelliviz.quakereport.QueryPreferences.DEFAULT_MAGNITUDE
import com.intelliviz.quakereport.QueryPreferences.SORT_DATE
import com.intelliviz.quakereport.QueryUtils.EXTRA_END_DATE
import com.intelliviz.quakereport.QueryUtils.EXTRA_MAX_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_MIN_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_START_DATE
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.EarthquakeEntity
import com.intelliviz.quakereport.db.EarthquakeInfoEntity
import com.intelliviz.quakereport.ui.EarthquakeOptionsDialog.Companion.EXTRA_SORT
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

const val ACTION_EARTHQUAKE_TREND = "action earthquake trend"
const val ACTION_EARTHQUAKE_RECENT = "action earthquake recent"
const val ACTION_EARTHQUAKE_RANGE = "action earthquake range"
const val BASEURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?"

class EarthquakeService : IntentService("EarthquakeService") {
    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            ACTION_EARTHQUAKE_TREND -> handleEarthquakeTrend(intent)
            ACTION_EARTHQUAKE_RECENT -> handleRecentEarthquake(intent)
            ACTION_EARTHQUAKE_RANGE -> handleRangeEarthquake(intent)
            else -> {}
        }
    }

    private fun loadDataFromURL(jsonURL: String?): String {
        val url = URL(jsonURL)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 10000
            connection.connect()
            val resCode = connection.responseCode
            val inStream: InputStream?
            if(resCode == HttpURLConnection.HTTP_OK) {
                inStream = connection.inputStream

                val br = BufferedReader(InputStreamReader(inStream))

                val jsonData = StringBuffer()
                var line: String?
                do {
                    line = br.readLine()
                    if (line == null) {
                        break
                    }
                    jsonData.append(line + "\n")
                } while (true)

                return jsonData.toString()
            } else if(resCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                return "ERROR"
            } else {
                return "ERROR"
            }
        } catch(e: MalformedURLException) {
            e.printStackTrace()
            return "ERROR"
        } catch(e: IOException) {
            e.printStackTrace()
            return "ERROR"
        } finally {
            connection.disconnect()
        }
    }

    private fun handleRecentEarthquake(intent: Intent) {
        getRangeEarthquake(intent)
    }

    private fun handleRangeEarthquake(intent: Intent) {
        getRangeEarthquake(intent)
    }

    private fun handleEarthquakeTrend(intent: Intent) {
        val db = AppDatabase.getAppDataBase(this)

        var year = intent.getIntExtra(QueryUtils.EXTRA_YEAR, 1900)
        val minMag = intent.getFloatExtra(EXTRA_MIN_MAG, DEFAULT_MAGNITUDE)
        val maxMag = intent.getFloatExtra(EXTRA_MAX_MAG, DEFAULT_MAGNITUDE) + 0.99F

        var startDate = year.toString()+"-1-1"
        var endDate = (year+10).toString()+"-1-1"
        var url: String = BASEURL + "format=geojson"
        if (!startDate.isEmpty()) {
            url = "$url&starttime=$startDate"
        }
        if (!endDate.isEmpty()) {
            url = "$url&endtime=$endDate" + "T23:59:59"
        }

        url = "$url&minmagnitude=$minMag"
        url = "$url&maxmagnitude=$maxMag"

        val jsonString = loadDataFromURL(url)
        var earthquakes: MutableList<EarthquakeEntity> = QueryUtils.extractEarthquakes(jsonString)
        if(earthquakes.size == 0) {

        }

        // key=year, <mag=key, count=value>
        val earthquakeTrends = mutableMapOf<Int, MutableMap<Float, Int>>()

        var years = mutableListOf<Int>()

        var currentYear = year
        while(true) {
            years.add(currentYear)
            currentYear += 10
            if(currentYear > 2020) {
                break
            }
        }


        //val years = arrayListOf(1900, 1910, 1920, 1930, 1940, 1950, 1960, 1970, 1980, 1990, 2000, 2010, 2020)
        years.forEach {
            val startYear = it
            val endStart = startYear + 10

            startDate = startYear.toString()+"-1-1"
            endDate = (endStart).toString()+"-1-1"

            earthquakes = getEarthquakes(startDate, endDate, minMag, maxMag)
            for (earthquake in earthquakes) {
                val mag = earthquake.magnitude
                year = QueryUtils.getYearFromDate(earthquake.date)
                addQuake(earthquakeTrends, year, mag)
            }
        }

        if(!earthquakeTrends.isEmpty()) {
            db?.beginTransaction()
            try {
                db?.earthquakeInfoDao()?.deleteAll()

                for(yearKey in earthquakeTrends.keys) {
                    val yearMap = earthquakeTrends[yearKey]
                    if(yearMap != null) {
                        for (magKey in yearMap.keys) {
                            val count = yearMap[magKey]
                            if(count != null) {
                                val entity = EarthquakeInfoEntity(yearKey, magKey, count)
                                db?.earthquakeInfoDao()?.insertEarthquakeInfo(entity)
                            }
                        }
                    }
                }
                db?.setTransactionSuccessful()
            } finally {
                db?.endTransaction()
            }
        }
    }

    private fun addQuake(quakeMap: MutableMap<Int, MutableMap<Float, Int>>, year: Int, mag: Float) {
        var yearMap = quakeMap[year]
        if(yearMap == null) {
            yearMap = mutableMapOf()
            yearMap[mag] = 1
            quakeMap[year] = yearMap
        } else {
            var count = yearMap[mag]
            if(count == null) {
                yearMap[mag] = 1
            } else {
                count++
                yearMap[mag] = count
            }
        }
    }

    private fun getEarthquakes(startDate: String, endDate: String, minMag: Float, maxMag: Float): MutableList<EarthquakeEntity>{
        var url: String = BASEURL + "format=geojson"
        if (!startDate.isEmpty()) {
            url = "$url&starttime=$startDate"
        }
        if (!endDate.isEmpty()) {
            url = "$url&endtime=$endDate" + "T23:59:59"
        }

        url = "$url&minmagnitude=$minMag"
        url = "$url&maxmagnitude=$maxMag"

        val jsonString = loadDataFromURL(url)
        return QueryUtils.extractEarthquakes(jsonString)
    }

    private fun getRangeEarthquake(intent: Intent) {
        val db = AppDatabase.getAppDataBase(this)

        val sort = intent.getIntExtra(EXTRA_SORT, SORT_DATE)
        val endDate = intent.getStringExtra(EXTRA_END_DATE)
        val startDate = intent.getStringExtra(EXTRA_START_DATE)
        val minMag = intent.getFloatExtra(EXTRA_MIN_MAG, DEFAULT_MAGNITUDE)
        val maxMag = intent.getFloatExtra(EXTRA_MAX_MAG, DEFAULT_MAGNITUDE) + .99F

        var url: String = BASEURL + "format=geojson"
        if (startDate != null && !startDate.isEmpty()) {
            url = "$url&starttime=$startDate"
        }
        if (endDate != null && !endDate.isEmpty()) {
            url = "$url&endtime=$endDate" + "T23:59:59"
        }

        url = "$url&minmagnitude=$minMag"
        url = "$url&maxmagnitude=$maxMag"

        var orderby = "&orderby=time"
        if(sort == QueryPreferences.SORT_MAG) {
            orderby = "&orderby=magnitude"
        }

        url = "$url$orderby"


        val jsonString = loadDataFromURL(url)

        val earthquakes: MutableList<EarthquakeEntity> = QueryUtils.extractEarthquakes(jsonString)
        val limit = 200

        if(!earthquakes.isEmpty()) {
            db?.beginTransaction()
            try {
                db?.earthquakeDao()?.deleteAll()

                for(item in earthquakes.indices) {
                    val earthquake = earthquakes[item]
                    db?.earthquakeDao()?.insertEarthquake(earthquake)
                    if(item > limit) {
                        break
                    }
                }
                db?.setTransactionSuccessful()
            } finally {
                db?.endTransaction()
            }
        }
    }
}