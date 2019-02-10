package com.intelliviz.quakereport.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface DownloadStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatus(status: DownloadStatusEntity)

    @Query("DELETE FROM " + DownloadStatusConstant.TABLE_NAME)
    fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateStatus(status: DownloadStatusEntity)

    @Query("SELECT * FROM " + DownloadStatusConstant.TABLE_NAME)
    fun getStatus(): LiveData<DownloadStatusEntity>
}