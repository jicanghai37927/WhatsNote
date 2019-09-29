package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.SavedStateTable;
import app.haiyunshan.whatsnote.record.entry.SavedStateEntry;

import java.util.List;

public class SavedStateEntity extends AbstractEntitySet<SavedStateEntity, SavedStateEntry> {

    SavedStateEntity(Context context, SavedStateEntry entry) {
        super(context, entry);
    }

    public void setOffset(String id, int top, int textOffset) {
        entry.setOffsetId(id);
        entry.setOffsetTop(top == 0? null: String.valueOf(top));
        entry.setTextOffset(textOffset == 0? null: String.valueOf(textOffset));
    }

    public void setFocus(String id, int start, int end) {
        entry.setFocusId(id);
        entry.setSelectionStart(start < 0? null: String.valueOf(start));
        entry.setSelectionEnd(end < 0? null: String.valueOf(end));
    }

    public String getOffsetId() {
        return entry.getOffsetId().orElse("");
    }

    public int getOffsetTop() {
        return entry.getOffsetTop().map(e -> Integer.parseInt(e)).orElse(0);
    }

    public int getTextOffset() {
        return entry.getTextOffset().map(e -> Integer.parseInt(e)).orElse(0);

    }

    public String getFocusId() {
        return entry.getFocusId().orElse("");

    }

    public int getSelectionStart() {
        return entry.getSelectionStart().map(e -> Integer.parseInt(e)).orElse(-1);
    }

    public int getSelectionEnd() {
        return entry.getSelectionEnd().map(e -> Integer.parseInt(e)).orElse(-1);
    }

    public void clear() {
        entry.setOffsetId(null);
        entry.setOffsetTop(null);
        entry.setTextOffset(null);

        entry.setFocusId(null);
        entry.setSelectionStart(null);
        entry.setSelectionEnd(null);
    }

    public long save() {
        return getManager().save(SavedStateTable.class);
    }

    public static final SavedStateEntity create(String id) {
        return Factory.create(WhatsApp.getInstance(), id);
    }

    public static final void delete(String id) {
        Factory.delete(WhatsApp.getInstance(), id);
    }

    /**
     *
     */
    static class Factory {

        static final SavedStateEntity create(final Context context, final String id) {
            RecordManager mgr = RecordManager.getInstance(context);

            SavedStateTable ds = mgr.obtainTable(SavedStateTable.class);
            SavedStateEntry entry = ds.get(id).orElseGet(() -> {
                SavedStateEntry e = new SavedStateEntry(id);
                ds.add(e);
                return e;
            });

            return new SavedStateEntity(context, entry);
        }

        static final void delete(Context context, String id) {
            RecordManager mgr = RecordManager.getInstance(context);

            SavedStateTable ds = mgr.obtainTable(SavedStateTable.class);
            ds.get(id).ifPresent(e -> ds.remove(e));
        }
    }

}
