package com.ixidev.simplenotepad;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ixidev.simplenotepad.adapters.NotesAdapter;
import com.ixidev.simplenotepad.callbacks.MainActionModeCallback;
import com.ixidev.simplenotepad.callbacks.NoteEventListener;
import com.ixidev.simplenotepad.db.NotesDB;
import com.ixidev.simplenotepad.db.NotesDao;
import com.ixidev.simplenotepad.model.Note;
import com.ixidev.simplenotepad.utils.NoteUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import static com.ixidev.simplenotepad.EditNoteActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NoteEventListener, Drawer.OnDrawerItemClickListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private NotesDao dao;
    private MainActionModeCallback actionModeCallback;
    private int chackedCount = 0;
    private FloatingActionButton fab;
    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupNavigation(savedInstanceState, toolbar);
        // init recyclerView
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // init fab Button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 13/05/2018  add new note
                onAddNewNote();
            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }

    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar) {

        // Navigation menu items
        List<IDrawerItem> iDrawerItems = new ArrayList<>();
        iDrawerItems.add(new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home_black_24dp));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Notes").withIcon(R.drawable.ic_note_black_24dp));

        // sticky DrawItems ; footer menu items

        List<IDrawerItem> stockyItems = new ArrayList<>();

        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem()
                .withName("Dark Theme")
                .withChecked(theme == R.style.AppTheme_Dark)
                .withIcon(R.drawable.ic_dark_theme)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        // TODO: 02/10/2018 change to darck theme and save it to settings
                        if (isChecked) {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme_Dark).apply();
                        } else {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme).apply();
                        }

                        // recreate app or the activity // if it's not working follow this steps
                        // MainActivity.this.recreate();

                        // this lines means wi want to close the app and open it again to change theme
                        TaskStackBuilder.create(MainActivity.this)
                                .addNextIntent(new Intent(MainActivity.this, MainActivity.class))
                                .addNextIntent(getIntent()).startActivities();
                    }
                });

        stockyItems.add(new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings_black_24dp));
        stockyItems.add(switchDrawerItem);

        // navigation menu header
        AccountHeader header = new AccountHeaderBuilder().withActivity(this)
                .addProfiles(new ProfileDrawerItem()
                        .withEmail("feedback.mrzero@gmail.com")
                        .withName("ixiDev")
                        .withIcon(R.mipmap.ic_launcher_round))
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .withSelectionListEnabledForSingleProfile(false) // we need just one profile
                .build();

        // Navigation drawer
        new DrawerBuilder()
                .withActivity(this) // activity main
                .withToolbar(toolbar) // toolbar
                .withSavedInstance(savedInstanceState) // saveInstance of activity
                .withDrawerItems(iDrawerItems) // menu items
                .withTranslucentNavigationBar(true)
                .withStickyDrawerItems(stockyItems) // footer items
                .withAccountHeader(header) // header of navigation
                .withOnDrawerItemClickListener(this) // listener for menu items click
                .build();

    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();// get All notes from DataBase
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(this, this.notes);
        // set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();
        // add swipe helper to recyclerView

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * when no notes show msg in main_layout
     */
    private void showEmptyView() {
        if (notes.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        }
    }

    /**
     * Start EditNoteActivity.class for Create New Note
     */
    private void onAddNewNote() {
        startActivity(new Intent(this, EditNoteActivity.class));

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
        Intent edit = new Intent(this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @Override
    public void onNoteLongClick(Note note) {
        // TODO: 22/07/2018 note long clicked : delete , share ..
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);

        // set new listener to adapter intend off MainActivity listener that we have implement
        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked(!note.isChecked()); // inverse selected
                if (note.isChecked())
                    chackedCount++;
                else chackedCount--;

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (chackedCount == 0) {
                    //  finish multi select mode wen checked count =0
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCount(chackedCount + "/" + notes.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

            }
        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    onShareNote();

                actionMode.finish();
                return false;
            }

        };

        // start action mode
        startActionMode(actionModeCallback);
        // hide fab button
        fab.setVisibility(View.GONE);
        actionModeCallback.setCount(chackedCount + "/" + notes.size());
    }

    private void onShareNote() {
        // TODO: 22/07/2018  we need share just one Note not multi

        Note note = adapter.getCheckedNotes().get(0);
        // TODO: 22/07/2018 do your logic here to share note ; on social or something else
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteText() + "\n\n Create on : " +
                NoteUtils.dateFromLong(note.getNoteDate()) + "\n  By :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);


    }

    private void onDeleteMultiNotes() {
        // TODO: 22/07/2018 delete multi notes

        List<Note> chackedNotes = adapter.getCheckedNotes();
        if (chackedNotes.size() != 0) {
            for (Note note : chackedNotes) {
                dao.deleteNote(note);
            }
            // refresh Notes
            loadNotes();
            Toast.makeText(this, chackedNotes.size() + " Note(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No Note(s) selected", Toast.LENGTH_SHORT).show();

        //adapter.setMultiCheckMode(false);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false); // uncheck the notes
        adapter.setListener(this); // set back the old listener
        fab.setVisibility(View.VISIBLE);
    }

    // swipe to right or to left te delete
    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // TODO: 28/09/2018 delete note when swipe

                    if (notes != null) {
                        // get swiped note
                        Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipedNote != null) {
                            swipeToDelete(swipedNote, viewHolder);

                        }

                    }
                }
            });

    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Delete Note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018 delete note
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018  Undo swipe and restore swipedNote
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                    }
                })
                .setCancelable(false)
                .create().show();

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        return false;
    }
}



