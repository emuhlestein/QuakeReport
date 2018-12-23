package com.intelliviz.quakereport.ui

interface EarthquakeData {
    fun loadEarthquakes(startDate: String, endDate: String, minMag: Int, maxMag: Int)
}