package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.RecentTable;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.database.TagTable;
import app.haiyunshan.whatsnote.record.entry.RecordEntry;
import app.haiyunshan.whatsnote.record.entry.TagEntry;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecentRecordSet extends AbstractEntitySet<RecordEntity, TagEntry> {

    RecentRecordSet(Context context, TagEntry entry) {
        super(context, entry);
    }

    public RecentRecordSet(@NonNull Context context, @Nullable TagEntry entry, @Nullable List<RecordEntity> list) {
        super(context, entry, list);
    }

    public long save() {
        return getManager().save();
    }

    public static final RecentRecordSet create(@Nullable String tagId) {
        return Factory.create(WhatsApp.getInstance(), tagId);
    }

    /**
     *
     */
    static class Factory {

        static final RecentRecordSet create(final Context context, final String tagId) {
            final RecordManager mgr = RecordManager.getInstance(context);
            final RecentTable ds = mgr.obtainTable(RecentTable.class);
            final RecordTable table = mgr.obtainTable(RecordTable.class);

            Stream<RecordEntry> stream = ds.getList().stream()
                    .map(e -> table.get(e.getId()))
                    .filter(e -> e.isPresent())
                    .map(e -> e.get());

            Optional<TagEntry> entry = mgr.obtainTable(TagTable.class).get(tagId);
            if (entry.isPresent()) {
                stream = stream.filter(e -> e.getTagList().indexOf(tagId) >= 0);
            }

            List<RecordEntity> list = stream.map(e -> new RecordEntity(context, e.getId(), e))
                    .filter(e -> !e.isTrash())
                    .collect(Collectors.toList());

            RecentRecordSet rs = new RecentRecordSet(context, entry.orElse(null), list);
            return rs;
        }
    }


}
