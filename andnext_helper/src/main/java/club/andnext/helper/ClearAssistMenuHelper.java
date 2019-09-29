package club.andnext.helper;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ClearAssistMenuHelper implements ActionMode.Callback {

    static final int ID_ASSIST = android.R.id.textAssist;

    int[] arguments;

    public static ClearAssistMenuHelper attach(TextView view) {
        ClearAssistMenuHelper helper = new ClearAssistMenuHelper();

        view.setCustomSelectionActionModeCallback(helper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setCustomInsertionActionModeCallback(helper);
        }

        return helper;
    }

    public void setArguments(int... arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        clearMenu(menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        clearMenu(menu);

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    void clearMenu(Menu menu) {

        {
            clearAssistMenuItems(menu);
        }

        if (arguments != null) {
            for (int id : arguments) {
                menu.removeItem(id);
            }
        }
    }

    public static void clearAssistMenuItems(Menu menu) {

        int i = 0;
        while (i < menu.size()) {
            final MenuItem menuItem = menu.getItem(i);
            int groupId = menuItem.getGroupId();
            int itemId = menuItem.getItemId();

            if (groupId == ID_ASSIST || itemId == Menu.NONE) {
                menu.removeItem(menuItem.getItemId());
                continue;
            }
            i++;
        }
    }

}
