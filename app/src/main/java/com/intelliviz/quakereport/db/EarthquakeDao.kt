package com.intelliviz.quakereport.db

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

interface EarthquakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEearthquake(earthquake: Earthquake)

    @Delete
    fun deleteEarthquake(earthquake: Earthquake)

    @Query("DELETE FROM Earthquake")
    fun deleteAll()

    @Query("SELECT * FROM Earthquake")
    fun getEarthquakes(): List<Earthquake>
}