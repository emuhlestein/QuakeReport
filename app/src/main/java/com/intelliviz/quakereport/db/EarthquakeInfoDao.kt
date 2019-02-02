package com.intelliviz.quakereport.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface EarthquakeInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEarthquakeInfo(earthquakeInfo: EarthquakeInfoEntity)

    @Query("DELETE FROM " + EarthquakeInfoConstant.TABLE_NAME)
    fun deleteAll()

    @Query("SELECT * FROM " + EarthquakeInfoConstant.TABLE_NAME)
    fun getEarthquakeInfo(): LiveData<List<EarthquakeInfoEntity>>
}