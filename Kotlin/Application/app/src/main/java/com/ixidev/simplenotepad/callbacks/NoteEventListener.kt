package com.ixidev.simplenotepad.callbacks

import com.ixidev.simplenotepad.entity.Note


/**
 * Created by ixi.Dv on 22/07/2018.
 */
interface NoteEventListener {
    /**
     * call wen note clicked.
     *
     * @param note: note item
     */
    fun onNoteClick(note: Note)

    /**
     * call wen long Click to note.
     *
     * @param note : item
     */
    fun onNoteLongClick(note: Note)
}