package com.intelliviz.quakereport.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.ContentValues
import android.content.Context
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.QueryUtils

@Database(entities = [EarthquakeEntity::class, EarthquakeQuery::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun earthquakeQueryDao(): EarthquakeQueryDao

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
                val values = ContentValues()
                val startDate = QueryUtils.getCurrentDate(30)
                val endDate = QueryUtils.getCurrentDate()
                values.put(EarthquakeQueryConstant.MODE, QueryPreferences.MODE_RANGE)
                values.put(EarthquakeQueryConstant.START_DATE, startDate)
                values.put(EarthquakeQueryConstant.END_DATE, endDate)
                values.put(EarthquakeQueryConstant.MIN_MAGNITUDE, QueryPreferences.MIN_MAG_DEFAULT)
                values.put(EarthquakeQueryConstant.MAX_MAGNITUDE, QueryPreferences.MAX_MAG_DEFAULT)
                values.put(EarthquakeQueryConstant.NUM_DAYS, 30)
                db.insert(EarthquakeQueryConstant.TABLE_NAME, OnConflictStrategy.IGNORE, values)
            }
        }
    }
}