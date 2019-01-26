package com.intelliviz.quakereport.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

object EarthquakeConstant {
        const val TABLE_NAME: String = "earthquake"
}

@Entity(tableName = EarthquakeConstant.TABLE_NAME)
data class EarthquakeEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val magnitude: Double,
        val distance: String,
        val city: String,
        val date: String,
        val time: String,
        val url: String)