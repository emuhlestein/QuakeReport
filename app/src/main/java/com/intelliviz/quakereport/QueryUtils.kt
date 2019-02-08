package com.intelliviz.quakereport

import android.util.Log
import com.intelliviz.quakereport.db.EarthquakeEntity
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object QueryUtils {

    const val EXTRA_MODE = "mode"
    const val EXTRA_START_DATE = "start_date"
    const val EXTRA_END_DATE = "end_date"
    const val EXTRA_MIN_MAG = "min_mag"
    const val EXTRA_MAX_MAG = "max_mag"
    const val EXTRA_YEAR = "year"
    const val EXTRA_NUM_DAYS = "num_days"

    /**
     * Return a list of [EarthquakeEntity] objects that has been built up from
     * parsing a JSON response.
     */
    fun extractEarthquakes(jsonStringResult: String?): MutableList<EarthquakeEntity> {
        if (jsonStringResult == null || jsonStringResult.isEmpty()) {
            return Collections.emptyList()
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        val earthquakes = ArrayList<EarthquakeEntity>()
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
                val eventType = properties.getString("type")
                if(eventType != "earthquake") {
                    Log.d("EDM", "Not a quake")
                }
                val tokens: List<String> = place.split(",")
                if(tokens.isEmpty()) {
                    earthquakes.add(EarthquakeEntity(mag.toFloat(), "", "", time, url))
                } else if(tokens.size == 1) {
                    earthquakes.add(EarthquakeEntity(mag.toFloat(), tokens[0], "", time, url))
                } else {
                    earthquakes.add(EarthquakeEntity(mag.toFloat(), tokens[0], tokens[1], time, url))
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

    fun getYearFromDate(time: Long): Int {
        val cal = Calendar.getInstance()
        val dateObject = Date(time)
        cal.time = dateObject
        return cal.get(Calendar.YEAR)
    }

    fun getCurrentDate(): String {
        return getCurrentDate(0)
    }

    fun getCurrentDate(numDays: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        var currentFormattedDate = dateFormat.format(currentDate)
        if(numDays > 0) {
            val c: Calendar = Calendar.getInstance()
            c.time = dateFormat.parse(currentFormattedDate)
            c.add(Calendar.DATE, -numDays)
            currentFormattedDate = dateFormat.format(c.time)
        }

        return currentFormattedDate
    }
}
