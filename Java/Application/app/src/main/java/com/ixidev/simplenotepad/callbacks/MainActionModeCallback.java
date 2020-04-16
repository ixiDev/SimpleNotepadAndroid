package com.ixidev.simplenotepad.callbacks;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ixidev.simplenotepad.R;

/**
 * Created by ixi.Dv on 22/07/2018.
 */
public abstract class MainActionModeCallback implements ActionMode.Callback {
    private ActionMode action;
    private MenuItem countItem;
    private MenuItem shareItem;

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.main_action_mode, menu);
        this.action = actionMode;
        this.countItem = menu.findItem(R.id.action_checked_count);
        this.shareItem = menu.findItem(R.id.action_share_note);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    public void setCount(String chackedCount) {
        if (countItem != null)
            this.countItem.setTitle(chackedCount);
    }

    /**
     * if checked count > 1 hide shareItem else show it
     *
     * @param b :visible
     */
    public void changeShareItemVisible(boolean b) {
        shareItem.setVisible(b);
    }

    public ActionMode getAction() {
        return action;
    }
}
