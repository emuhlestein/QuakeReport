package com.intelliviz.quakereport.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "earthquake")
data class Earthquake(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val magnitude: Double,
        val distance: String,
        val city: String,
        val date: String,
        val time: String,
        val url: String)