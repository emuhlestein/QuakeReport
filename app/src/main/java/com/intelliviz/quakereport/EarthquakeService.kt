package com.intelliviz.quakereport

import android.app.IntentService
import android.content.Intent
import com.intelliviz.quakereport.QueryUtils.EXTRA_END_DATE
import com.intelliviz.quakereport.QueryUtils.EXTRA_MAX_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_MIN_MAG
import com.intelliviz.quakereport.QueryUtils.EXTRA_START_DATE
import com.intelliviz.quakereport.db.AppDatabase
import com.intelliviz.quakereport.db.Earthquake
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class EarthquakeService : IntentService("EarthquakeService") {
    override fun onHandleIntent(intent: Intent?) {
        val db = AppDatabase.getAppDataBase(this)

        val endDate = intent?.getStringExtra(EXTRA_END_DATE)
        val startDate = intent?.getStringExtra(EXTRA_START_DATE)
        val minMag = intent?.getIntExtra(EXTRA_MIN_MAG, 0)
        val maxMag = intent?.getIntExtra(EXTRA_MAX_MAG, 0)

        // TODO read earth quakes
        val baseURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?"
        var url: String = baseURL + "format=geojson"
        if(endDate != null && !endDate?.isEmpty()) {
            url = url + "&endtime=" + endDate
        }
        if(startDate != null && !startDate.isEmpty()) {
            url = url + "&starttime=" + startDate
        }
        if(minMag != null) {
            url = url + "&minmag=" + minMag
        }

        val jsonString = loadDataFromURL(url)


        val earthquakes: MutableList<Earthquake> = QueryUtils.extractEarthquakes(jsonString)

        // TODO save earthquakes to data base
        db?.beginTransaction()
        try {
            db?.earthquakeDao()?.deleteAll()

            earthquakes.forEach { earthquake ->
                db?.earthquakeDao()?.insertEarthquake(earthquake)
            }
            db?.setTransactionSuccessful()
        }finally {
            db?.endTransaction()
        }
    }

    // TODO is copied from GetEarthquakeDataAsyncTask
    fun loadDataFromURL(jsonURL: String?): String {
        try {
            val url = URL(jsonURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doInput = true
            connection.connect()
            val br = BufferedReader(InputStreamReader(connection.inputStream))

            val jsonData = StringBuffer()
            var line: String?
            do {
                line = br.readLine()
                if(line == null) { break }
                jsonData.append(line + "\n")
            } while(true)
            return jsonData.toString()
        } catch(e: MalformedURLException) {
            e.printStackTrace()
            return "ERROR"
        } catch(e: IOException) {
            e.printStackTrace()
            return "ERROR"
        }
    }
}