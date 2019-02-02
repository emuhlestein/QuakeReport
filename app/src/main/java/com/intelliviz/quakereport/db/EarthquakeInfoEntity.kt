package com.intelliviz.quakereport.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

object EarthquakeInfoConstant {
    const val TABLE_NAME: String = "earthquakeinfo"
}

@Entity(tableName = EarthquakeInfoConstant.TABLE_NAME)
data class EarthquakeInfoEntity (
        val year: Int,
        val magnitude: Int,
        val count: Int,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)