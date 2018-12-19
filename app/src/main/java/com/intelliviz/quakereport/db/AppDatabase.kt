package com.intelliviz.quakereport.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Earthquake::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun earthquakeDao(): EarthquakeDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    if(INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "EarthquakeDB").build()
                    }
                }
            }
            return INSTANCE
        }
    }
}