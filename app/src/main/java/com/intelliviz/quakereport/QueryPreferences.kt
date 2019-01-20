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
    val NUM_DAYS: String = "NumDays"
    val YEAR: String = "year"
    val MIN_MAG_DEFAULT: Int = 1
    val NUM_DAYS_DEFAULT: Int = 30
    val YEAR_DEFAULT: Int = 1900
    val MODE_DEFAULT: Int = 0
    val MODE: String = "mode"
    val RECENT: Int = 0
    val RANGE: Int = 1

    fun getMode(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MODE, MODE_DEFAULT)
    }

    fun setMode(context: Context, mode: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MODE, mode)
        editor.apply()
    }

    fun getStartDate(context: Context): String {
        val currentDate = getCurrentDate(0)
        return PreferenceManager.getDefaultSharedPreferences(context).getString(START_DATE, currentDate)
    }

    fun setStartDate(context: Context, startDate: String) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(START_DATE, startDate)
        editor.apply()
    }

    fun getEndDate(context: Context): String {
        val currentDate = getCurrentDate(1)
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

    fun getNumDays(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(NUM_DAYS, NUM_DAYS_DEFAULT)
    }

    fun setNumDays(context: Context, maxMag: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(NUM_DAYS, maxMag)
        editor.apply()
    }

    fun getYear(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(YEAR, YEAR_DEFAULT)
    }

    fun setYear(context: Context, year: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(YEAR, year)
        editor.apply()
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