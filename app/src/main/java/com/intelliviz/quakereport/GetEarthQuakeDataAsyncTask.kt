package com.intelliviz.quakereport

import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class GetEarthQuakeDataAsyncTask(_listener: GetEarthQuakeDataAsyncTask.OnEarthquakeLoadListener) : AsyncTask<String?, Void, String>() {


    private var listener: OnEarthquakeLoadListener? = null

    init {
        listener = _listener
    }

    interface OnEarthquakeLoadListener {
        fun onEarthquakeLoad(earthquakes: List<Earthquake>)
    }

    override fun doInBackground(vararg p0: String?): String {
       var data: String = loadDataFromURL(p0[0])
        return data
    }

    override fun onPostExecute(result: String?) {
        if(result == null) {
            return
        }

        var jsonData: JSONObject = JSONObject(result)
        var earthQuakes: JSONArray = jsonData.getJSONArray("features")


        val earthquakes: MutableList<Earthquake> = QueryUtils.extractEarthquakes(result)

        listener?.onEarthquakeLoad(earthquakes)
    }


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
