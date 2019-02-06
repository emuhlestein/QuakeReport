package com.intelliviz.quakereport.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

object EarthquakeConstant {
        const val TABLE_NAME: String = "earthquake"
}

@Entity(tableName = EarthquakeConstant.TABLE_NAME)
data class EarthquakeEntity(
        val magnitude: Float,
        val distance: String,
        val city: String,
        val date: Long,
        val url: String,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)