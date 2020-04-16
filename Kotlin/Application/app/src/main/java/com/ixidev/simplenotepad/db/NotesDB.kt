package com.ixidev.simplenotepad.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ixidev.simplenotepad.entity.Note

/**
 * Created by ixi.Dv on 20/06/2018.
 */
@Database(entities = [Note::class], version = 1)
abstract class NotesDB : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    companion object {
        private const val DATABSE_NAME = "notesDb"
        private var instance: NotesDB? = null
        fun getInstance(context: Context): NotesDB {
            if (instance == null) instance =
                Room.databaseBuilder(
                    context,
                    NotesDB::class.java,
                    DATABSE_NAME
                )
                    .allowMainThreadQueries()
                    .build()
            return instance as NotesDB
        }
    }
}