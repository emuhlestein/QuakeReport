package com.intelliviz.quakereport

import android.app.IntentService
import android.content.Intent
import com.intelliviz.quakereport.QueryUtils.EXTRA_END_DATE
import com.intelliviz.quakereport.QueryUtils.EXTRA_MAX_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_MIN_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_NUM_DAYS
import com.intelliviz.quakereport.QueryUtils.EXTRA_START_DATE
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.EarthquakeEntity
import com.intelliviz.quakereport.ui.EarthquakeOptionsDialog.Companion.EXTRA_MODE
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
        //if(needToDownloadRecent(intent)) {
            getRangeEarthquake(intent)
        //}
    }

    private fun handleRangeEarthquake(intent: Intent) {
        //if(needToDownloadRange(intent)) {
            getRangeEarthquake(intent)
        //}
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
            url = "$url + &starttime= + $startDate"
        }
        if (endDate != null && !endDate.isEmpty()) {
            url = "$url + &endtime= + $endDate + T23:59:59"
        }

        url = "$url + &minmagnitude= + $minMag"
        url = "$url + &maxmagnitude= + $maxMag"


        val jsonString = loadDataFromURL(url)

        val earthquakes: MutableList<EarthquakeEntity> = QueryUtils.extractEarthquakes(jsonString)

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

    private fun needToDownloadRange(intent: Intent): Boolean {
        val mode = intent.getIntExtra(EXTRA_MODE, 0)
        val endDate = intent.getStringExtra(EXTRA_END_DATE)
        val startDate = intent.getStringExtra(EXTRA_START_DATE)
        val minMag = intent.getIntExtra(EXTRA_MIN_MAG, 0)
        val maxMag = intent.getIntExtra(EXTRA_MAX_MAG, 0)
        val currentMode = QueryPreferences.getMode(this)
        val currentMinMag = QueryPreferences.getMinMag(this)
        val currentMaxMag = QueryPreferences.getMaxMag(this)
        val currentEndDate = QueryPreferences.getEndDate(this)
        val currentStartDate = QueryPreferences.getStartDate(this)

        return if(mode != currentMode ||
                endDate != currentEndDate || startDate != currentStartDate ||
                minMag != currentMinMag || maxMag != currentMaxMag) {
            QueryPreferences.setMode(this, mode)
            QueryPreferences.setMinMag(this, minMag)
            QueryPreferences.setMaxMag(this, maxMag)
            QueryPreferences.setStartDate(this, startDate)
            QueryPreferences.setEndDate(this, endDate)
            true
        } else {
            false
        }
    }

    private fun needToDownloadRecent(intent: Intent): Boolean {
        val mode = intent.getIntExtra(EXTRA_MODE, 0)
        val numDays = intent.getIntExtra(EXTRA_NUM_DAYS, 0)
        val minMag = intent.getIntExtra(EXTRA_MIN_MAG, 0)
        val maxMag = intent.getIntExtra(EXTRA_MAX_MAG, 0)
        val currentMode = QueryPreferences.getMode(this)
        val currentMinMag = QueryPreferences.getMinMag(this)
        val currentMaxMag = QueryPreferences.getMaxMag(this)
        val currentNumDays = QueryPreferences.getNumDays(this)

        return if(mode != currentMode || numDays != currentNumDays ||
                minMag != currentMinMag || maxMag != currentMaxMag) {
            QueryPreferences.setMode(this, mode)
            QueryPreferences.setMinMag(this, minMag)
            QueryPreferences.setMaxMag(this, maxMag)
            QueryPreferences.setNumDays(this, numDays)
            true
        } else {
            false
        }
    }
}