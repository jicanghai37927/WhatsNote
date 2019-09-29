package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.entry.RecordEntry;

import java.util.Optional;

class RecordUtils {

    static final String getName(RecordEntry e) {
        if (e == null) {
            return "";
        }

        if (!TextUtils.isEmpty(e.getName())) {
            return e.getName();
        }

        if (!TextUtils.isEmpty(e.getAlias())) {
            return e.getAlias();
        }

        return "";
    }

    static String getName(Context context, RecordEntry entry, String parentId, boolean combine) {

        String name = getName(entry);

        if (entry.getParent().equals(parentId)) {
            return name;
        }

        if (!RecordUtils.findByName(context, parentId, entry.getId(), name).isPresent()) {
            return name;
        }

        int count = 2;
        if (combine) {
            int pos = name.lastIndexOf(' ');
            if ((pos >= 0) && ((pos + 1) != name.length())) {
                String postfix = name.substring(pos + 1);
                int number = -1;
                try {
                    number = Integer.parseInt(postfix);
                } catch (Exception e) {

                }

                if (number > 0) {
                    name = name.substring(0, pos);
                }

                count = (number > count) ? number : count;
            }
        }

        while (true) {
            String text = name + " " + count;
            if (!RecordUtils.findByName(context, parentId, entry.getId(), text).isPresent()) {
                return text;
            }

            ++count;
        }
    }

    static Optional<RecordEntry> findByName(final Context context, final String parentId, final String id, final String name) {
        RecordManager mgr = RecordManager.getInstance(context);
        RecordTable ds = mgr.obtainTable(RecordTable.class);

        return ds.getList().stream()
                .filter(e -> !(e.getId().equals(id)))
                .filter(e -> e.getParent().equals(parentId))
                .filter(e -> RecordUtils.getName(e).equals(name))
                .findAny();
    }

    static boolean isDescendantOf(RecordTable table, RecordEntry entry, String id) {
        RecordEntry ancestor = entry;
        if (ancestor == null) {
            return false;
        }

        RecordEntry child;

        do {
            child = ancestor;

            if (ancestor.getId().equals(id)) {
                return true;
            }
        } while((ancestor = getParent(table, ancestor)) != null);

        return child.getParent().equals(id);
    }

    static RecordEntry getParent(RecordTable table, RecordEntry entry) {
        if (entry == null) {
            return null;
        }

        String parentId = entry.getParent();
        entry = table.get(parentId).orElse(null);
        return entry;
    }
}
