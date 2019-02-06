package com.intelliviz.quakereport

data class Earthquake(val magnitude: Float,
                      val distance: String, val city: String, val date: String,
                      val time: String, val url: String)