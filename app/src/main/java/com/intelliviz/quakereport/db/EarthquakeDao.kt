package com.intelliviz.quakereport.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface EarthquakeDao {
    @Insert(onConflict = REPLACE)
    fun insertEarthquake(earthquake: EarthquakeEntity)

    @Query("DELETE FROM " + EarthquakeConstant.TABLE_NAME)
    fun deleteAll()

    @Query("SELECT * FROM " + EarthquakeConstant.TABLE_NAME + " ORDER BY date DESC")
    fun getEarthquakes(): LiveData<List<EarthquakeEntity>>
}