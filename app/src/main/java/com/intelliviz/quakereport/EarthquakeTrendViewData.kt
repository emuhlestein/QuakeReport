package com.intelliviz.quakereport

import com.intelliviz.quakereport.graphview.PointValue

/**
 *
 */
data class EarthquakeTrendViewData(var values: ArrayList<PointValue>, var colors: HashMap<Int, Int>)