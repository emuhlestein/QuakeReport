package com.intelliviz.quakereport

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.intelliviz.quakereport.QueryUtils.getCurrentDate

object QueryPreferences {
    private const val START_DATE: String = "StartDate"
    private const val END_DATE: String = "EndDate"
    private const val MIN_MAG: String = "MinMag"
    private const val MAX_MAG: String = "MaxMag"
    private const val NUM_DAYS: String = "NumDays"
    private const val YEAR: String = "year"
    private const val MIN_MAG_DEFAULT: Int = 6
    private const val MAX_MAG_DEFAULT: Int = 7
    private const val NUM_DAYS_DEFAULT: Int = 30
    private const val YEAR_DEFAULT: Int = 1900
    private const val MODE: String = "mode"
    private const val MODE_RECENT: Int = 0
    private const val MODE_RANGE: Int = 1
    private const val MODE_DEFAULT: Int = MODE_RANGE

    fun getMode(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MODE, MODE_DEFAULT)
    }

    fun setMode(context: Context, mode: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MODE, mode)
        editor.apply()
    }

    fun getStartDate(context: Context): String {
        val currentDate: String = getCurrentDate(0)
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
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MAX_MAG, MAX_MAG_DEFAULT)
    }

    fun setMaxMag(context: Context, maxMag: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(MAX_MAG, maxMag)
        editor.apply()
    }

    fun getNumDays(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(NUM_DAYS, NUM_DAYS_DEFAULT)
    }

    fun setNumDays(context: Context, numDays: Int) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(NUM_DAYS, numDays)
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

}