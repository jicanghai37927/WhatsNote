package app.haiyunshan.whatsnote.base;

import android.view.MenuItem;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.function.Consumer;

public class MenuItemClickListener implements Toolbar.OnMenuItemClickListener,
        PopupMenu.OnMenuItemClickListener,
        MenuItem.OnMenuItemClickListener {

    HashMap<Integer, Consumer<Integer>> map;

    public MenuItemClickListener() {
        this.map = new HashMap<>();
    }

    public void clear() {
        map.clear();
    }

    public MenuItemClickListener put(int itemId, Consumer<Integer> consumer) {
        map.put(itemId, consumer);

        return this;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onMenuItemClick(item.getItemId());
    }

    public boolean onMenuItemClick(final int itemId) {
        Consumer consumer = map.computeIfPresent(itemId, (id, action) -> {
            action.accept(itemId);
            return action;
        });

        return (consumer != null);
    }
}
