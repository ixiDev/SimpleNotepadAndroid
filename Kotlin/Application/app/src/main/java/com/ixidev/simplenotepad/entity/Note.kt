package com.ixidev.simplenotepad.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by ABDELMAJID ID ALI on 16/04/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "text")
    var noteText: String,
    @ColumnInfo(name = "date")
    var noteDate: Long
) {
    @Ignore
    var checked: Boolean = false
}