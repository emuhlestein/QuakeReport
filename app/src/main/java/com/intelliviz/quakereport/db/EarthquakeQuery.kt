package com.intelliviz.quakereport.db


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.intelliviz.quakereport.db.EarthquakeQueryConstant.END_DATE
import com.intelliviz.quakereport.db.EarthquakeQueryConstant.MAX_MAGNITUDE
import com.intelliviz.quakereport.db.EarthquakeQueryConstant.MIN_MAGNITUDE
import com.intelliviz.quakereport.db.EarthquakeQueryConstant.MODE
import com.intelliviz.quakereport.db.EarthquakeQueryConstant.NUM_DAYS
import com.intelliviz.quakereport.db.EarthquakeQueryConstant.START_DATE

object EarthquakeQueryConstant {
        const val TABLE_NAME: String = "earthquakequery"
        const val MODE: String = "mode"
        const val MIN_MAGNITUDE: String = "min_magnitude"
        const val MAX_MAGNITUDE: String = "max_magnitude"
        const val START_DATE: String = "start_date"
        const val END_DATE: String = "end_date"
        const val NUM_DAYS: String = "num_days"
}

@Entity(tableName = EarthquakeQueryConstant.TABLE_NAME)
data class EarthquakeQuery(
        @PrimaryKey(autoGenerate = true)
        val id: Int,

        @ColumnInfo(name = MODE)
        val mode: Int,

        @ColumnInfo(name = MIN_MAGNITUDE)
        val minMagnitude: Double,

        @ColumnInfo(name = MAX_MAGNITUDE)
        val maxMagnitude: Double,

        @ColumnInfo(name = START_DATE)
        val startDate: String,

        @ColumnInfo(name = END_DATE)
        val endDate: String,

        @ColumnInfo(name = NUM_DAYS)
        val numDays: String)