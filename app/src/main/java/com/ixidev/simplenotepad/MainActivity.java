package com.ixidev.simplenotepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ixidev.simplenotepad.adapters.NotesAdapter;
import com.ixidev.simplenotepad.callbacks.NoteEventListener;
import com.ixidev.simplenotepad.db.NotesDB;
import com.ixidev.simplenotepad.db.NotesDao;
import com.ixidev.simplenotepad.model.Note;
import com.ixidev.simplenotepad.utils.NoteUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ixidev.simplenotepad.EditeNoteActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NoteEventListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private NotesDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // init recyclerView
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // init fab Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 13/05/2018  add new note
                onAddNewNote();
            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();// get All notes from DataBase
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(this, this.notes);
        // set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
    }

    /**
     * Start EditNoteActivity.class for Create New Note
     */
    private void onAddNewNote() {
        startActivity(new Intent(this, EditeNoteActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    public void onNoteClick(Note note) {
        // TODO: 22/07/2018  note clicked : edit note
        Intent edit = new Intent(this, EditeNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @Override
    public void onNoteLongClick(final Note note) {
        // TODO: 22/07/2018 note long clicked : delete , share ..

        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 22/07/2018  delete Note from database adn refresh
                        dao.deleteNote(note); // delete
                        loadNotes(); // refresh notes

                    }
                })
                .setNegativeButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 22/07/2018   share note text
                        Intent share = new Intent(Intent.ACTION_SEND);
                        // Make your logic to share note
                        String text = note.getNoteText() + "\n Create on :" +
                                NoteUtils.dateFromLong(note.getNoteDate())
                                + " By :" + getString(R.string.app_name);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(share);

                    }
                })
                .create()
                .show();

    }
}
