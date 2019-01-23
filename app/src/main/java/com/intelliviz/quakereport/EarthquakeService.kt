package com.intelliviz.quakereport

import android.app.IntentService
import android.content.Intent
import com.intelliviz.quakereport.QueryUtils.EXTRA_END_DATE
import com.intelliviz.quakereport.QueryUtils.EXTRA_MAX_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_MIN_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_START_DATE
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.Earthquake
import com.intelliviz.quakereport.db.EarthquakeQuery
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

class EarthquakeService : IntentService("EarthquakeService") {
    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            ACTION_EARTHQUAKE_TREND -> handleEarthquakeTrend()
            ACTION_EARTHQUAKE_RECENT -> handleRecentEarthquake(intent)
            ACTION_EARTHQUAKE_RANGE -> handleRangeEarthquake(intent)
            else -> {}
        }
    }

    // TODO is copied from GetEarthquakeDataAsyncTask
    fun loadDataFromURL(jsonURL: String?): String {
        val url = URL(jsonURL)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 10000
            connection.connect()
            val resCode = connection.responseCode
            var inStream: InputStream? = null
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
        val numDays = intent.getIntExtra(EXTRA_END_DATE, 0)
        val endDate = intent.getStringExtra(EXTRA_END_DATE)
        val startDate = intent.getStringExtra(EXTRA_START_DATE)
        val minMag = intent.getIntExtra(EXTRA_MIN_MAG, 0)
        val maxMag = intent.getIntExtra(EXTRA_MAX_MAG, 0)
        val db = AppDatabase.getAppDataBase(this)
        val query: EarthquakeQuery? = db?.earthquakeQueryDao()?.get()

        if(numDays != query?.numDays ||
           endDate != query?.endDate ||
           startDate != query?.startDate ||
                        minMag != query.minMagnitude ||
                        maxMag != query.maxMagnitude ) {
            getRangeEarthquake(intent)
        }
    }

    private fun handleRangeEarthquake(intent: Intent) {
        val endDate = intent.getStringExtra(EXTRA_END_DATE)
        val startDate = intent.getStringExtra(EXTRA_START_DATE)
        val minMag = intent.getIntExtra(EXTRA_MIN_MAG, 0)
        val maxMag = intent.getIntExtra(EXTRA_MAX_MAG, 0)
        val db = AppDatabase.getAppDataBase(this)
        val query: EarthquakeQuery? = db?.earthquakeQueryDao()?.get()

        if(endDate != query?.endDate ||
           startDate != query?.startDate ||
           minMag != query?.minMagnitude ||
           maxMag != query?.maxMagnitude ) {
            getRangeEarthquake(intent)
        }
    }

    private fun handleEarthquakeTrend() {

    }

    private fun getRangeEarthquake(intent: Intent) {
        val db = AppDatabase.getAppDataBase(this)

        val endDate = intent.getStringExtra(EXTRA_END_DATE)
        val startDate = intent.getStringExtra(EXTRA_START_DATE)
        val minMag = intent.getIntExtra(EXTRA_MIN_MAG, 0)
        val maxMag = intent.getIntExtra(EXTRA_MAX_MAG, 0)

        val baseURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?"
        var url: String = baseURL + "format=geojson"
        if (startDate != null && !startDate.isEmpty()) {
            url = url + "&starttime=" + startDate
        }
        if (endDate != null && !endDate.isEmpty()) {
            url = url + "&endtime=" + endDate
        }
        if (minMag != null) {
            url = url + "&minmagnitude=" + minMag
        }
        if (maxMag != null) {
            url = url + "&maxmagnitude=" + maxMag
        }

        val jsonString = loadDataFromURL(url)

        val earthquakes: MutableList<Earthquake> = QueryUtils.extractEarthquakes(jsonString)

        if(!earthquakes.isEmpty()) {
            db?.beginTransaction()
            try {
                db?.earthquakeDao()?.deleteAll()

                earthquakes.forEach { earthquake ->
                    db?.earthquakeDao()?.insertEarthquake(earthquake)
                }
                db?.setTransactionSuccessful()
            } finally {
                db?.endTransaction()
            }
        }
    }

    private fun needToReloadRecent(intent: Intent, query: EarthquakeQuery) {


    }
}