package com.intelliviz.quakereport

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object QueryUtils {

    /**
     * Return a list of [Earthquake] objects that has been built up from
     * parsing a JSON response.
     */
    fun extractEarthquakes(jsonStringResult: String?): MutableList<Earthquake> {
        if (jsonStringResult == null || jsonStringResult.isEmpty()) {
            return Collections.emptyList()
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        val earthquakes = ArrayList<Earthquake>()
        try {
            val jsonObject = JSONObject(jsonStringResult)
            val jsonEarthquakes = jsonObject.getJSONArray("features")

            for (i in 0 until jsonEarthquakes.length()) {
                val item = jsonEarthquakes.getJSONObject(i)
                val properties = item.getJSONObject("properties")
                val mag = properties.getDouble("mag")
                val place = properties.getString("place")
                val time = properties.getLong("time")
                val dateString = getDateToDisplay(time)
                earthquakes.add(Earthquake(mag, place, dateString))
            }
        } catch (e: JSONException) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e)
        }

        return earthquakes
    }

    fun getDateToDisplay(time: Long): String {
        var dateObject = Date(time)
        var dateFormatter: SimpleDateFormat = SimpleDateFormat("MMM DD, yyyy")
        return dateFormatter.format(dateObject)
    }
}
