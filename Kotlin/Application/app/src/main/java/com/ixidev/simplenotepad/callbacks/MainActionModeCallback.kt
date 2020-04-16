package com.ixidev.simplenotepad.callbacks

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import com.ixidev.simplenotepad.R

/**
 * Created by ixi.Dv on 22/07/2018.
 */
public abstract class MainActionModeCallback : ActionMode.Callback {
    var action: ActionMode? = null
        private set
    private var countItem: MenuItem? = null
    private var shareItem: MenuItem? = null
    override fun onCreateActionMode(
        mode: ActionMode?,
        menu: Menu?
    ): Boolean {
        mode?.let {
            it.menuInflater.inflate(R.menu.main_action_mode, menu)
            action = it
            countItem = menu!!.findItem(R.id.action_checked_count)
            shareItem = menu.findItem(R.id.action_share_note)
        }
        return true
    }

    override fun onPrepareActionMode(
        actionMode: ActionMode,
        menu: Menu
    ): Boolean {
        return false
    }

    override fun onDestroyActionMode(actionMode: ActionMode) {}


    fun setCount(chackedCount: String?) {
        countItem?.apply {
            title = chackedCount
        }
    }

    /**
     * if checked count > 1 hide shareItem else show it
     *
     * @param b :visible
     */
    fun changeShareItemVisible(b: Boolean) {
        shareItem?.apply {
            isVisible = b
        }
    }

}