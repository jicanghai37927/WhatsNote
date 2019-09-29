package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.database.TagTable;
import app.haiyunshan.whatsnote.record.entry.TagEntry;
import app.haiyunshan.whatsnote.tag.entity.ColorEntity;
import club.andnext.base.BaseEntity;
import club.andnext.base.EntityList;
import club.andnext.utils.ListUtils;
import club.andnext.utils.UUIDUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TagEntity extends AbstractEntitySet<TagEntity, TagEntry> {

    String name;

    ColorEntity color;

    TagEntity(Context context, TagEntry entry) {
        super(context, entry);
    }

    public TagEntity(@NonNull Context context, @Nullable TagEntry entry, @Nullable List<TagEntity> list) {
        super(context, entry, list);
    }

    TagEntity(TagEntity entity) {
        this(entity.context, entity.entry);

        this.color = (entity.getColor());
        this.name = entity.getName();

        if (!entity.getList().isEmpty()) {
            this.list = entity.getList().stream()
                    .map(e -> new TagEntity(e))
                    .collect(Collectors.toList());
        }
    }

    public ColorEntity getColor() {
        if (color != null) {
            return color;
        }

        this.color = Optional.ofNullable(entry)
                .map(e -> e.getColor())
                .map(c -> ColorEntity.obtain().get(c).get())
                .orElse(ColorEntity.TRANSPARENT);

        return color;
    }

    public void setColor(ColorEntity color) {
        this.color = color;

        if (entry != null) {
            entry.setColor(color.getId());
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        }

        this.name = Optional.ofNullable(entry).map(e -> e.getName()).orElse("");
        return name;
    }

    public void setName(String name) {
        this.name = name;

        if (entry != null) {
            entry.setName(name);
        }
    }

    public TagEntity add(String name, String color) {

        int index = 0;

        TagEntry entry = new TagEntry(UUIDUtils.next(), name, color);

        TagEntity entity = new TagEntity(this.context, entry);

        //
        this.add(index, entity);

        // add to table
        this.getManager().obtainTable(TagTable.class).add(index, entity.getEntry().get());

        return entity;

    }

    public boolean move(final String fromId, final String toId) {
        int i = this.indexOf(fromId);
        int j = this.indexOf(toId);

        if (i < 0 || j < 0) {
            return false;
        }

        ListUtils.move(this.getList(), i, j);
        ListUtils.move(getManager().obtainTable(TagTable.class).getList(), i, j);

        return true;
    }

    public int indexOfName(String name) {
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }

        return -1;
    }

    public long countOfRecord(final boolean includeTrash) {
        final String id = this.getId();
        final RecordTable ds = getManager().obtainTable(RecordTable.class);

        return ds.getList().stream()
                .filter(e -> e.getTagList().indexOf(id) >= 0)
                .map(e -> new RecordEntity(context, e.getId(), e))
                .filter(e -> includeTrash? true: !e.isTrash())
                .count();
    }

    public void removeFromRecord() {
        final String id = this.getId();
        final RecordTable ds = getManager().obtainTable(RecordTable.class);

        ds.getList().stream()
                .forEach(e -> e.getTagList().remove(id));

        getManager().save(RecordTable.class);
    }

    public TagEntity delete(String id) {
        TagEntity entity = super.remove(id);
        if (entity != null) {
            getManager().obtainTable(TagTable.class).remove(entity.getEntry().get());
        }

        return entity;
    }

    @Override
    public EntityList toList() {
        return new EntityList(new TagEntity(this).list);
    }

    public long save() {
        return getManager().save(TagTable.class, RecordTable.class);
    }

    @Override
    public boolean areContentsTheSame(BaseEntity obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TagEntity)) {
            return false;
        }

        TagEntity another = (TagEntity)obj;

        if (!this.getId().equals(another.getId())) {
            return false;
        }

        boolean result = true;
        if (result) {
            String name1 = this.getName();
            String name2 = another.getName();

            result = name1.equals(name2);
        }

        if (result) {
            result = (this.getColor() == another.getColor());
        }

        return result;
    }

    public static final TagEntity obtain() {
        return Factory.obtain(WhatsApp.getInstance());
    }

    /**
     *
     */
    static class Factory {

        public static final TagEntity obtain(final Context context) {
            final RecordManager mgr = RecordManager.getInstance(context);

            return mgr.obtainEntity(TagEntity.class, e -> {
               TagEntity entity = create(context);
               mgr.put(entity);
               return entity;
            });
        }

        static final TagEntity create(final Context context) {

            final RecordManager mgr = RecordManager.getInstance(context);
            final TagTable ds = mgr.obtainTable(TagTable.class);

            final List<TagEntity> list = ds.getList().stream()
                    .map(e -> new TagEntity(context, e))
                    .collect(Collectors.toList());

            final TagEntity entity = new TagEntity(context, null, list);

            return entity;
        }

        static final Optional<TagEntity> create(final Context context, final String id) {
            final RecordManager mgr = RecordManager.getInstance(context);
            final TagTable ds = mgr.obtainTable(TagTable.class);

            return ds.get(id).map(e -> new TagEntity(context, e));
        }
    }

}
