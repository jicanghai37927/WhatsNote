package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.EntranceTable;
import app.haiyunshan.whatsnote.record.entry.EntranceEntry;
import club.andnext.base.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

public class EntranceEntity extends AbstractEntitySet<EntranceEntity, EntranceEntry> {

    public static final String ID_NOTE      = "note";
    public static final String ID_RECENT    = "recent";
    public static final String ID_TRASH     = "trash";

    EntranceEntity(Context context, EntranceEntry entry) {
        super(context, entry);
    }

    public EntranceEntity(@NonNull Context context, @NonNull EntranceEntry entry, @Nullable List<EntranceEntity> list) {
        super(context, entry, list);
    }

    public String getName() {
        return entry.getName();
    }

    public long save() {
        return 0;
    }

    @Override
    public boolean areContentsTheSame(BaseEntity obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof EntranceEntity)) {
            return false;
        }

        EntranceEntity another = (EntranceEntity)obj;

        if (!this.getId().equals(another.getId())) {
            return false;
        }

        boolean result = true;
        if (result) {
            String name1 = this.getName();
            String name2 = another.getName();

            result = name1.equals(name2);
        }

        return result;
    }

    public static final EntranceEntity obtain() {
        return Factory.obtain(WhatsApp.getInstance());
    }

    /**
     *
     */
    static class Factory {

        public static final EntranceEntity obtain(final Context context) {
            final RecordManager mgr = RecordManager.getInstance(context);

            return mgr.obtainEntity(EntranceEntity.class, e -> {
               EntranceEntity entity = create(context);
               mgr.put(entity);
               return entity;
            });
        }

        static final EntranceEntity create(final Context context) {

            final RecordManager mgr = RecordManager.getInstance(context);
            final EntranceTable ds = mgr.obtainTable(EntranceTable.class);

            final List<EntranceEntity> list = ds.getList().stream()
                    .map(e -> new EntranceEntity(context, e))
                    .collect(Collectors.toList());

            final EntranceEntity entity = new EntranceEntity(context, null, list);

            return entity;
        }
    }

}
