package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.SortTable;
import app.haiyunshan.whatsnote.record.entry.SortEntry;
import club.andnext.base.BaseEntity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortEntity extends AbstractEntitySet<SortEntity, SortEntry> {

    public static final String ID_NAME      = "name";
    public static final String ID_CREATED   = "created";
    public static final String ID_MODIFIED  = "modified";
    public static final String ID_SIZE      = "size";
    public static final String ID_TAG       = "tag";

    boolean reverse;
    Comparator<RecordEntity>[] array;

    SortEntity(Context context, SortEntry entry) {
        super(context, entry);

        this.reverse = false;
        this.array = new Comparator[2];
    }

    public SortEntity(@NonNull Context context, @Nullable SortEntry entry, @Nullable List<SortEntity> list) {
        super(context, entry, list);
    }

    SortEntity(SortEntity entity) {
        super(entity.context, entity.entry);

        this.entry = entity.entry;
        this.reverse = entity.reverse;
        this.array = Arrays.copyOf(entity.array, entity.array.length);
    }

    public String getName() {
        return entry.getName();
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setReverse(boolean value) {
        this.reverse = value;
    }

    public void toggle() {
        this.setReverse(!isReverse());
    }

    public Comparator<RecordEntity> getComparator() {
        int index = (reverse)? 1: 0;
        if (array[index] != null) {
            return array[index];
        }

        if (array[0] == null) {
            String id = getId();
            array[0] = getManager().obtainComparator(id);
        }

        if (index == 1) {
            array[index] = array[0].reversed();
        }

        return array[index];
    }

    public long save() {
        return 0;
    }

    @Override
    public boolean areContentsTheSame(BaseEntity obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SortEntity)) {
            return false;
        }

        SortEntity another = (SortEntity)obj;

        if (!this.getId().equals(another.getId())) {
            return false;
        }

        boolean b = !(this.isReverse() ^ another.isReverse());
        return b;
    }

    public static final SortEntity listAll() {
        return Factory.all(WhatsApp.getInstance());
    }

    public static final SortEntity create(String id) {
        return Factory.create(WhatsApp.getInstance(), id);
    }

    /**
     *
     */
    static class Factory {

        public static final SortEntity all(final Context context) {
            return create(context);
        }

        public static final SortEntity create(Context context, String id) {
            SortTable ds = RecordManager.getInstance(context).obtainTable(SortTable.class);
            SortEntry entry = ds.get(id).orElse(ds.get(0));

            return new SortEntity(context, entry);
        }

        public static final SortEntity create(SortEntity entity) {
            return new SortEntity(entity);
        }

        static final SortEntity create(Context context) {
            RecordManager mgr = RecordManager.getInstance(context);

            SortTable ds = mgr.obtainTable(SortTable.class);

            List<SortEntity> list = ds.getList().stream()
                    .filter(e -> e.isVisible())
                    .map(e -> new SortEntity(context, e))
                    .collect(Collectors.toList());

            SortEntity entity = new SortEntity(context, null, list);
            return entity;
        }
    }

}
