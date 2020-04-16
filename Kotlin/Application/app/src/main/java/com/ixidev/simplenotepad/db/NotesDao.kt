package com.ixidev.simplenotepad.db

import androidx.room.*
import com.ixidev.simplenotepad.entity.Note

/**
 * Notes Data Object access help to access the notes
 * Created by ixi.Dv on 20/06/2018.
 */
@Dao
interface NotesDao {
    /**
     * Insert and save note to Database
     *
     * @param note
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note): Long

    /**
     * Delete note
     *
     * @param note that will be delete
     */
    @Delete
    fun deleteNote(vararg note: Note)

    /**
     * Update note
     *
     * @param note the note that will be update
     */
    @Update
    fun updateNote(note: Note)

    /**
     * List All Notes From Database
     *
     * @return list of Notes
     */
    @get:Query("SELECT * FROM notes")
    val notes: List<Note>

    /**
     * @param noteId note id
     * @return Note
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteById(noteId: Int): Note

    /**
     * Delete Note by Id from DataBase
     *
     * @param noteId
     */
    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Int)
}