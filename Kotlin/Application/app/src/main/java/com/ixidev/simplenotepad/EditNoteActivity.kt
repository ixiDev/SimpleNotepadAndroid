package com.ixidev.simplenotepad

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.ixidev.simplenotepad.db.NotesDB
import com.ixidev.simplenotepad.db.NotesDao
import com.ixidev.simplenotepad.entity.Note
import com.ixidev.simplenotepad.utils.APP_PREFERENCES
import com.ixidev.simplenotepad.utils.THEME_Key
import kotlinx.android.synthetic.main.activity_edit_note.*
import java.util.*


class EditNoteActivity : AppCompatActivity() {

    private lateinit var inputNote: EditText
    private lateinit var dao: NotesDao
    private var temp: Note? = null

    companion object {
        val NOTE_EXTRA_Key = "note_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        setSupportActionBar(edit_note_activity_toolbar)
        inputNote = input_note
        dao = NotesDB.getInstance(this).notesDao()

        if (intent.extras != null) {
            val id = intent.extras!!.getInt(NOTE_EXTRA_Key, 0)
            temp = dao.getNoteById(id)
            temp?.let {
                inputNote.setText(it.noteText)
            }
        } else inputNote.isFocusable = true
    }

    private fun setupTheme() {
        // set theme
        val sharedPreferences =
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val theme = sharedPreferences.getInt(THEME_Key, R.style.AppTheme)
        setTheme(theme)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edite_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.save_note) onSaveNote()
        return super.onOptionsItemSelected(item)
    }

    private fun onSaveNote() { // TODO: 20/06/2018 Save Note
        val text = inputNote.toText()
        if (text.isNotEmpty()) {
            val date: Long = Date().time // get  system time
            // if  exist update els crete new
            if (temp == null) {
                temp = Note(0, text, date)
                dao.insertNote(temp!!) // create new note and inserted to database
            } else {
                temp!!.noteText = text
                temp!!.noteDate = date
                dao.updateNote(temp!!) // change text and date and update note on database
            }
            finish() // return to the MainActivity
        }
    }


    private fun EditText.toText(): String {
        return this.text.toString()
    }
}
