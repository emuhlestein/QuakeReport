package com.intelliviz.quakereport.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [EarthquakeEntity::class, EarthquakeInfoEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun earthquakeInfoDao(): EarthquakeInfoDao

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
            }
        }
    }
}