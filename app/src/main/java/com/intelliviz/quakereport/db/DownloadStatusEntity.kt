package com.intelliviz.quakereport.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

object DownloadStatusConstant {
    const val TABLE_NAME: String = "download_status"
    const val COLUMN_PROGRESS: String = "progress"
    const val COLUMN_STATUS: String = "status"
    const val DOWNLOAD_STATUS_BEGIN: Int = 1
    const val DOWNLOAD_STATUS_END: Int = 0
    const val DOWNLOAD_STATUS_INPROGRESS: Int = 2
    const val DOWNLOAD_PROGRESS: Int = 0
}

@Entity(tableName = DownloadStatusConstant.TABLE_NAME)
data class DownloadStatusEntity(
            val status: Int,
            val progress: Int,
            @PrimaryKey(autoGenerate = true) val id: Long = 0)