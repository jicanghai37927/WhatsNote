package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.FavoriteTable;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.entry.FavoriteEntry;
import app.haiyunshan.whatsnote.record.entry.RecordEntry;
import club.andnext.base.BaseEntity;
import club.andnext.base.EntityList;
import club.andnext.utils.ListUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FavoriteEntity extends AbstractEntitySet<FavoriteEntity, FavoriteEntry> {

    String name;
    Boolean isTrash = null;

    FavoriteEntity(Context context, FavoriteEntry entry) {
        this(context, entry, null);
    }

    public FavoriteEntity(@NonNull Context context, @Nullable FavoriteEntry entry, @Nullable List<FavoriteEntity> list) {
        super(context, entry, list);
    }

    FavoriteEntity(FavoriteEntity entity) {
        this(entity.context, entity.entry);

        this.name = entity.getName();
        this.isTrash = entity.isTrash();

        if (entity.list != null) {
            this.list = entity.list.stream()
                    .map(e -> new FavoriteEntity(e))
                    .collect(Collectors.toList());
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        }

        // always get this value from entry, should not cache it

        String instantName = Optional.ofNullable(entry).map(e -> {
            Optional<RecordEntry> entry = getManager().obtainTable(RecordTable.class).get(e.getId());
            String text = entry.map(RecordUtils::getName).orElse("");
            return text;
        }).orElse("");

        return instantName;
    }

    public boolean isTrash() {
        if (isTrash != null) {
            return isTrash;
        }

        // always get this value from entry, should not cache it

        boolean instantTrash = RecordEntity.create(this.getId())
                .map(e -> e.isTrash())
                .orElse(true);

        return instantTrash;
    }

    public FavoriteEntity add(String id) {
        Optional<FavoriteEntity> result = this.get(id);

        return result.orElseGet(() -> {
            int index = 0;

            FavoriteEntry entry = new FavoriteEntry(id);
            FavoriteEntity entity = new FavoriteEntity(this.context, entry);

            //
            this.add(index, entity);

            //
            getManager().obtainTable(FavoriteTable.class).add(index, entity.getEntry().get());

            return entity;
        });
    }

    public boolean move(final String fromId, final String toId) {
        int i = this.indexOf(fromId);
        int j = this.indexOf(toId);

        if (i < 0 || j < 0) {
            return false;
        }

        ListUtils.move(this.getList(), i, j);
        ListUtils.move(getManager().obtainTable(FavoriteTable.class).getList(), i, j);

        return true;
    }

    public FavoriteEntity delete(String id) {
        FavoriteEntity entity = super.remove(id);
        if (entity != null) {
            getManager().obtainTable(FavoriteTable.class).remove(entity.getEntry().get());
        }

        return entity;
    }

    @Override
    public EntityList toList() {
        return new EntityList(new FavoriteEntity(this).getList().stream()
                .filter(e -> !e.isTrash())
                .collect(Collectors.toList()));
    }

    public long save() {
        return getManager().save(FavoriteTable.class);
    }

    @Override
    public boolean areContentsTheSame(BaseEntity obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FavoriteEntity)) {
            return false;
        }

        FavoriteEntity another = (FavoriteEntity)obj;

        if (!this.getId().equals(another.getId())) {
            return false;
        }

        boolean a = this.getName().equals(another.getName());
        boolean b = (this.isTrash() == another.isTrash());

        return (a && b);
    }

    public static final FavoriteEntity obtain() {
        return Factory.obtain(WhatsApp.getInstance());
    }

    /**
     *
     */
    static class Factory {

        static final FavoriteEntity obtain(Context context) {
            RecordManager mgr = RecordManager.getInstance(context);
            return mgr.obtainEntity(FavoriteEntity.class, e -> {
                FavoriteEntity entity = create(context);
                mgr.put(entity);
                return entity;
            });
        }

        static final FavoriteEntity create(Context context) {

            RecordManager mgr = RecordManager.getInstance(context);
            FavoriteTable ds = mgr.obtainTable(FavoriteTable.class);
            List<FavoriteEntity> list = ds.getList().stream()
                    .map(e -> new FavoriteEntity(context, e))
                    .collect(Collectors.toList());

            FavoriteEntity entity = new FavoriteEntity(context, null, list);
            return entity;
        }

    }
}

