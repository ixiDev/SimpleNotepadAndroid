package com.ixidev.simplenotepad.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ixidev.simplenotepad.R
import com.ixidev.simplenotepad.adapters.NotesAdapter.NoteHolder
import com.ixidev.simplenotepad.callbacks.NoteEventListener
import com.ixidev.simplenotepad.entity.Note
import com.ixidev.simplenotepad.utils.NoteUtils
import kotlinx.android.synthetic.main.note_layout.view.*

/**
 * Created by ixi.Dv on 13/05/2018.
 */
class NotesAdapter(
    private val context: Context,
    private var notes: List<Note>
) :
    RecyclerView.Adapter<NoteHolder>() {
    private var listener: NoteEventListener? = null
    private var multiCheckMode = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false)
        return NoteHolder(v)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note: Note? = getNote(position)
        note?.let {
            holder.noteText.text = it.noteText
            holder.noteDate.text = NoteUtils.dateFromLong(it.noteDate)
            // init note click event
            holder.itemView.setOnClickListener { listener!!.onNoteClick(note) }
            // init note long click
            holder.itemView.setOnLongClickListener {
                listener!!.onNoteLongClick(note)
                false
            }
            // check checkBox if note selected
            if (multiCheckMode) {
                holder.checkBox.visibility = View.VISIBLE // show checkBox if multiMode on
                holder.checkBox.isChecked = note.checked
            } else holder.checkBox.visibility = View.GONE // hide checkBox if multiMode off
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    private fun getNote(position: Int): Note? {
        return notes[position]
    }

    /**
     * get All checked notes
     *
     * @return Array
     */
    fun getCheckedNotes(): List<Note> = notes.filter { note ->
        note.checked
    }


    inner class NoteHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var noteText: TextView
        var noteDate: TextView
        var checkBox: AppCompatCheckBox

        init {
            itemView.apply {
                noteText = note_text
                noteDate = note_date
                checkBox = check_Box
            }
        }
        /* var noteText: TextView = itemView.findViewById(R.id.note_text)
         var noteDate: TextView = itemView.findViewById(R.id.note_date)
         var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)*/

    }

    fun setListener(listener: NoteEventListener?) {
        this.listener = listener
    }

    fun setMultiCheckMode(multiCheckMode: Boolean) {
        this.multiCheckMode = multiCheckMode
        if (!multiCheckMode)
            notes.forEach {
                it.checked = false
            }
        notifyDataSetChanged()
    }

}