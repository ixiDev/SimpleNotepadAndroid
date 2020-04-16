package com.ixidev.simplenotepad

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.ixidev.simplenotepad.EditNoteActivity.Companion.NOTE_EXTRA_Key
import com.ixidev.simplenotepad.adapters.NotesAdapter
import com.ixidev.simplenotepad.callbacks.MainActionModeCallback
import com.ixidev.simplenotepad.callbacks.NoteEventListener
import com.ixidev.simplenotepad.db.NotesDB
import com.ixidev.simplenotepad.db.NotesDao
import com.ixidev.simplenotepad.entity.Note
import com.ixidev.simplenotepad.utils.APP_PREFERENCES
import com.ixidev.simplenotepad.utils.NoteUtils.dateFromLong
import com.ixidev.simplenotepad.utils.THEME_Key
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SwitchDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener, NoteEventListener {
    private lateinit var settings: SharedPreferences
    private var themeId = 0
    private lateinit var dao: NotesDao
    private var notes = ArrayList<Note>()
    lateinit var adapter: NotesAdapter
    private var chackedCount = 0
    private lateinit var actionModeCallback: MainActionModeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupDrawerMenu(savedInstanceState)
        dao = NotesDB.getInstance(this).notesDao()

        fab.setOnClickListener {
            //  Start EditNoteActivity.class for Create New Note
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }

    private fun setupTheme() {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        themeId = settings.getInt(THEME_Key, R.style.AppTheme)
        setTheme(themeId)
    }

    private fun setupDrawerMenu(savedInstanceState: Bundle?) {
        // navigation menu header
        val header: AccountHeader = AccountHeaderBuilder().withActivity(this).apply {
            addProfiles(
                ProfileDrawerItem()
                    .withEmail("feedback.mrzero@gmail.com")
                    .withName("ixiDev")
                    .withIcon(R.mipmap.ic_launcher_round)
            )
            withSavedInstance(savedInstanceState)
            withHeaderBackground(R.drawable.ic_launcher_background)
            withSelectionListEnabledForSingleProfile(false) // we need just one profile
        }.build()

        val switchDrawerItem = SwitchDrawerItem().apply {
            withName("Dark Theme")
            withChecked(themeId == R.style.AppTheme_Dark)
            withIcon(R.drawable.ic_dark_theme)
            onCheckedChangeListener = object : OnCheckedChangeListener {
                override fun onCheckedChanged(
                    drawerItem: IDrawerItem<*>,
                    buttonView: CompoundButton,
                    isChecked: Boolean
                ) {
                    val tm = if (isChecked) R.style.AppTheme_Dark else R.style.AppTheme
                    settings.edit().apply {
                        putInt(THEME_Key, tm)
                        apply()
                    }
                    recreate()
                    /* TaskStackBuilder.create(this@MainActivity)
                            .addNextIntent(Intent(this@MainActivity,
                                    MainActivity::class.java))
                            .addNextIntent(intent).startActivities()*/
                }
            }
        }

        // Navigation drawer
        DrawerBuilder().apply {
            withActivity(this@MainActivity) // activity main
            withToolbar(toolbar) // toolbar
            withSavedInstance(savedInstanceState) // saveInstance of activity
            withTranslucentNavigationBar(true)
            withStickyDrawerItems(arrayListOf(switchDrawerItem)) // footer items
            withAccountHeader(header) // header of navigation
            withOnDrawerItemClickListener(this@MainActivity) // listener for menu items click
        }.build()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        notes.apply {
            clear()
            addAll(dao.notes)
        }
        notes_recycler_view.let {
            adapter = NotesAdapter(this, notes)
            adapter.setListener(this)
            it.adapter = adapter
        }
        showEmptyView()
    }

    /**
     * when no notes show msg in main_layout
     */
    private fun showEmptyView() {
        if (notes.size == 0) {
            notes_recycler_view.visibility = View.GONE
            empty_notes_view.visibility = View.VISIBLE
        } else {
            notes_recycler_view.visibility = View.VISIBLE
            empty_notes_view.visibility = View.GONE
        }
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
        return false
    }

    override fun onNoteClick(note: Note) {
        //  note clicked : edit note
        val edit = Intent(this, EditNoteActivity::class.java)
        edit.putExtra(NOTE_EXTRA_Key, note.id)
        startActivity(edit)
    }

    // note long clicked : delete , share ..
    override fun onNoteLongClick(note: Note) {
        note.checked = true
        chackedCount = 1
        adapter.setMultiCheckMode(true)
        actionModeCallback = object : MainActionModeCallback() {
            override fun onActionItemClicked(
                actionMode: ActionMode,
                menuItem: MenuItem
            ): Boolean {

                if (menuItem.itemId == R.id.action_delete_notes)
                    onDeleteMultiNotes()
                else if (menuItem.itemId == R.id.action_share_note)
                    onShareNote()

                actionMode.finish()
                return false
            }
        }

        // set new listener to adapter intend off MainActivity listener that we have implement
        adapter.setListener(object : NoteEventListener {
            override fun onNoteClick(note: Note) {
                note.checked = !note.checked // inverse selected
                // count
                if (note.checked) chackedCount++ else chackedCount--

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false)
                } else actionModeCallback.changeShareItemVisible(true)
                actionModeCallback.setCount(chackedCount.toString() + "/" + notes.size)
                adapter.notifyDataSetChanged()
            }

            override fun onNoteLongClick(note: Note) {}
        })

        // start action mode
        startSupportActionMode(actionModeCallback)
        // hide fab button
        fab.visibility = View.GONE
        actionModeCallback.setCount(chackedCount.toString() + "/" + notes.size)
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        adapter.setMultiCheckMode(false) // uncheck the notes
        adapter.setListener(this) // set back the old listener
        fab.visibility = View.VISIBLE
    }

    private fun onShareNote() {
        //  we need share just one Note not multi
        val note: Note = adapter.getCheckedNotes()[0]
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        val notetext: String = note.noteText + "\n\n Create on : " +
                dateFromLong(note.noteDate) + "\n  By :" +
                getString(R.string.app_name)
        share.putExtra(Intent.EXTRA_TEXT, notetext)
        startActivity(share)
    }

    private fun onDeleteMultiNotes() { // TODO: 22/07/2018 delete multi notes
        val chackedNotes: List<Note> = adapter.getCheckedNotes()
        if (chackedNotes.isNotEmpty()) {
            // delete all checked notes
            dao.deleteNote(*chackedNotes.toTypedArray())
            // refresh Notes
            loadNotes()
            Toast.makeText(
                this, "${chackedNotes.size} Note(s) Delete successfully !", Toast.LENGTH_SHORT
            ).show()
        } else Toast.makeText(this, "No Note(s) selected", Toast.LENGTH_SHORT).show()
        //adapter.setMultiCheckMode(false);
    }


}
