package com.intelliviz.quakereport

import android.util.Log
import com.intelliviz.quakereport.db.Earthquake
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object QueryUtils {

    val EXTRA_START_DATE = "start_date"
    val EXTRA_END_DATE = "end_date"
    val EXTRA_MIN_MAG = "min_mag"
    val EXTRA_MAX_MAG = "max_mag"
    val EXTRA_YEAR = "year"
    val EXTRA_NUM_DAYS = "num_days"

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
                val url = properties.getString("url")
                val dateString = getDateToDisplay(time)
                val timeString = getTimeToDisplay(time)
                val tokens: List<String> = place.split(",")
                if(tokens.size == 0) {
                    earthquakes.add(Earthquake(0, mag, "", "", dateString, timeString, url))
                } else if(tokens.size == 1) {
                    earthquakes.add(Earthquake(0, mag, tokens[0], "", dateString, timeString, url))
                } else {
                    earthquakes.add(Earthquake(0, mag, tokens[0], tokens[1], dateString, timeString, url))
                }
            }
        } catch (e: JSONException) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e)
        }

        return earthquakes
    }

    fun getDateToDisplay(time: Long): String {
        val dateObject = Date(time)
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy")
        return dateFormatter.format(dateObject)
    }

    fun getTimeToDisplay(time: Long): String {
        val dateObject = Date(time)
        val timeFormatter = SimpleDateFormat("h:mm a")
        return timeFormatter.format(dateObject)
    }
}
