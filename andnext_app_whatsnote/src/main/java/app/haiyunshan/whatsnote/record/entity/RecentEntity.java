package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.RecentTable;
import app.haiyunshan.whatsnote.record.entry.RecentEntry;

import java.util.List;
import java.util.Optional;

public class RecentEntity extends AbstractEntitySet<RecentEntity, RecentEntry> {

    RecentEntity(Context context, RecentEntry entry) {
        super(context, entry);
    }

    public long save() {
        return getManager().save(RecentTable.class);
    }

    public static final RecentEntity create(String id) {
        return Factory.create(WhatsApp.getInstance(), id);
    }

    public static final void delete(String id) {
        Factory.delete(WhatsApp.getInstance(), id);
    }

    /**
     *
     */
    static class Factory {

        static final RecentEntity create(Context context, String id) {
            RecentTable ds = RecordManager.getInstance(context).obtainTable(RecentTable.class);

            {
                Optional<RecentEntry> entry = ds.get(id);
                entry.ifPresent(e -> ds.remove(e));
            }

            RecentEntry entry = new RecentEntry(id);
            ds.add(0, entry);

            return new RecentEntity(context, entry);
        }

        static final void delete(Context context, String id) {
            RecentTable ds = RecordManager.getInstance(context).obtainTable(RecentTable.class);

            {
                Optional<RecentEntry> entry = ds.get(id);
                entry.ifPresent(e -> ds.remove(e));
            }
        }
    }

}
