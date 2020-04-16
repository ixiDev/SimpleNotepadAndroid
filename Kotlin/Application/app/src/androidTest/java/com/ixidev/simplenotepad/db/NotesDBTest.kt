package com.ixidev.simplenotepad.db

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ixidev.simplenotepad.entity.Note
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Created by ABDELMAJID ID ALI on 16/04/2020.
 * Email : abdelmajid.idali@gmail.com
 * Github : https://github.com/ixiDev
 */

@RunWith(AndroidJUnit4::class)
class NotesDBTest {
    private val TAG = "NotesDBTest"
    @Test
    fun testDao() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val dao = NotesDB.getInstance(appContext).notesDao()
        val time = Date().time
        val insertNote = dao.insertNote(Note(0, "test", time))
        Log.i(TAG, "insertNote : $insertNote");
        assert(insertNote > 0)
        /* val noteById = dao.getNoteById(insertNote.toInt())
         assertEquals(time, noteById.noteDate)*/
    }

    @Test
    fun testGatAllNotes() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dao = NotesDB.getInstance(appContext).notesDao()
        val notes = dao.notes

        Log.i(TAG, "siz : ${notes.size}");
        assert(notes.isNotEmpty())

    }

}