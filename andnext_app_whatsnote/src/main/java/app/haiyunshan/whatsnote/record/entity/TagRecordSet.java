package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.database.TagTable;
import app.haiyunshan.whatsnote.record.entry.RecordEntry;
import app.haiyunshan.whatsnote.record.entry.TagEntry;
import app.haiyunshan.whatsnote.tag.entity.ColorEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagRecordSet extends AbstractEntitySet<RecordEntity, TagEntry> {

    TagRecordSet(Context context, TagEntry entry) {
        super(context, entry);
    }

    public TagRecordSet(@NonNull Context context, @Nullable TagEntry entry, @Nullable List<RecordEntity> list) {
        super(context, entry, list);
    }

    public String getName() {
        if (entry == null) {
            return "";
        }

        return entry.getName();
    }

    public int getColor() {
        if (entry == null) {
            return Color.TRANSPARENT;
        }

        ColorEntity color = ColorEntity.obtain().get(entry.getColor())
                .orElse(ColorEntity.TRANSPARENT);

        return color.getIcon();
    }

    public long save() {
        return getManager().save();
    }

    public static final TagRecordSet create(String id) {
        return Factory.create(WhatsApp.getInstance(), id);
    }

    /**
     *
     */
    static class Factory {

        public static final TagRecordSet create(Context context, String id) {

            final RecordManager mgr = RecordManager.getInstance(context);
            Optional<TagEntry> entry = mgr.obtainTable(TagTable.class).get(id);
            if (!entry.isPresent()) {
                return new TagRecordSet(context, null, new ArrayList<>());
            }

            final RecordTable ds = mgr.obtainTable(RecordTable.class);

            Stream<RecordEntry> stream = ds.getList().stream();
            stream = stream.filter(e -> e.getTagList().indexOf(id) >= 0);
            List<RecordEntity> list = stream.map(e -> new RecordEntity(context, e.getId(), e))
                    .filter(e -> !e.isTrash())
                    .collect(Collectors.toList());

            TagRecordSet rs = new TagRecordSet(context, entry.orElse(null), list);

            return rs;
        }

    }

}
