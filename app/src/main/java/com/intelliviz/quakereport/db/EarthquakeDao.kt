package com.intelliviz.quakereport.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface EarthquakeDao {
    @Insert(onConflict = REPLACE)
    fun insertEarthquake(earthquake: Earthquake)

    @Delete
    fun deleteEarthquake(earthquake: Earthquake)

    @Query("DELETE FROM Earthquake")
    fun deleteAll()

    @Query("SELECT * FROM Earthquake")
    fun getEarthquakes(): LiveData<List<Earthquake>>
}