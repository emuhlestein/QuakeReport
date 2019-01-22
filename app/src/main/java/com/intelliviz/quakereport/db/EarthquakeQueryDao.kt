package com.intelliviz.quakereport.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface EarthquakeQueryDao {
    @Insert(onConflict = REPLACE)
    fun insert(query: EarthquakeQuery)

    @Update(onConflict = REPLACE)
    fun update(query: EarthquakeQuery)

    @Query("SELECT * FROM " + EarthquakeQueryConstant.TABLE_NAME)
    fun get(): EarthquakeQuery
}