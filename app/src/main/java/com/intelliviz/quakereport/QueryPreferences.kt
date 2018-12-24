package com.intelliviz.quakereport

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

object QueryPreferences {
    val START_DATE: String = "StartDate"
    val END_DATE: String = "EndDate"
    val MIN_MAG: String = "MinMag"
    val MAX_MAG: String = "MaxMag"
    val MIN_MAG_DEFAULT: Int = 1

    fun getStartDate(context: Context): String {
        val currentDate = getCurrentDate(false)
        return PreferenceManager.getDefaultSharedPreferences(context).getString(START_DATE, currentDate)
    }

    fun setStartDate(context: Context, startDate: String) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(START_DATE, startDate)
        editor.apply()
    }

    fun getEndDate(context: Context): String {
        val currentDate = getCurrentDate(true)
        return PreferenceManager.getDefaultSharedPreferences(context).getString(END_DATE, currentDate)
    }

    fun setEndDate(context: Context, endDate: String) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(END_DATE, endDate)
        editor.apply()
    }

    fun getMinMag(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MIN_MAG, MIN_MAG_DEFAULT)
    }

    fun setMinMag(context: Context, minMag: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MIN_MAG, minMag)
        editor.apply()
    }

    fun getMaxMag(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MAX_MAG, MIN_MAG_DEFAULT)
    }

    fun setMaxMag(context: Context, maxMag: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MAX_MAG, maxMag)
        editor.apply()
    }

    private fun getCurrentDate(addDay: Boolean): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        var currentFormattedDate = dateFormat.format(currentDate)
        if(addDay) {
            val c: Calendar = Calendar.getInstance()
            c.time = dateFormat.parse(currentFormattedDate)
            c.add(Calendar.DATE, 1)
            currentFormattedDate = dateFormat.format(c.time)
        }

        return currentFormattedDate
    }
}