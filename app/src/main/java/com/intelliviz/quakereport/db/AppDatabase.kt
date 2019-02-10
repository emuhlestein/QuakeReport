package com.intelliviz.quakereport.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.intelliviz.quakereport.db.DownloadStatusConstant.DOWNLOAD_STATUS_END

@Database(entities = [EarthquakeEntity::class,
    EarthquakeInfoEntity::class, DownloadStatusEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun earthquakeInfoDao(): EarthquakeInfoDao
    abstract fun downloadStatusDao(): DownloadStatusDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    if(INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "EarthquakeDB")
                                .addCallback(CALLBACK)
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        private val CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
//                val values = ContentValues()
//                values.put(DownloadStatusConstant.COLUMN_PROGRESS, 0)
//                values.put(DownloadStatusConstant.COLUMN_STATUS, 0)
//                db.insert(DownloadStatusConstant.TABLE_NAME, OnConflictStrategy.IGNORE, values)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                //UpdateStatusTask(INSTANCE!!.downloadStatusDao()).execute()
            }
        }
    }

    private class UpdateStatusTask(var dao: DownloadStatusDao) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(objects: Array<Void>): Void? {
            var status = dao.getStatus()
            if(status.value == null) {
                Log.d("EDM", "Status is null")
                dao.insertStatus(DownloadStatusEntity(DOWNLOAD_STATUS_END, 0))
            }
            status = dao.getStatus()
            if(status.value == null) {
                Log.d("EDM", "Status is null")
            }
            dao.updateStatus(DownloadStatusEntity(DOWNLOAD_STATUS_END, 0))

            return null
        }
    }
}