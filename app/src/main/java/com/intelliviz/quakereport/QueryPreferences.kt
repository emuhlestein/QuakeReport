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
        return PreferenceManager.getDefaultSharedPreferences(context).getString(START_DATE, "2016-05-02")
    }

    fun setStartDate(context: Context, startDate: String) {
        var editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(START_DATE, startDate)
        editor.apply()
    }

    fun getEndDate(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(END_DATE, "2016-05-03")
    }

    fun setEndDate(context: Context, endDate: String) {
        var editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(END_DATE, endDate)
        editor.apply()
    }

    fun getMinMag(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MIN_MAG, MIN_MAG_DEFAULT)
    }

    fun setMinMag(context: Context, minMag: Int) {
        var editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MIN_MAG, minMag)
        editor.apply()
    }

    fun getMaxMag(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MAX_MAG, MIN_MAG_DEFAULT)
    }

    fun setMaxMag(context: Context, maxMag: Int) {
        var editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MAX_MAG, maxMag)
        editor.apply()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}