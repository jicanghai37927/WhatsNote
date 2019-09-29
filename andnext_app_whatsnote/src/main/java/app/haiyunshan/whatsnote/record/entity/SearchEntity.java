package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.database.SearchTable;
import app.haiyunshan.whatsnote.record.entry.SearchEntry;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SearchEntity extends AbstractEntitySet<SearchEntity, SearchEntry> {

    SearchEntity(Context context, SearchEntry entry) {
        super(context, entry);
    }

    public SearchEntity(@NonNull Context context, @Nullable SearchEntry entry, @Nullable List<SearchEntity> list) {
        super(context, entry, list);
    }

    public String getName() {
        String name = Optional.ofNullable(entry)
                .map(e -> getManager().obtainTable(RecordTable.class).get(e.getId()).orElse(null))
                .map(RecordUtils::getName)
                .orElse("");
        return name;
    }

    public void add(RecordEntity entity) {
        String id = entity.getId();
        if (TextUtils.isEmpty(id)) {
            return;
        }

        Factory.add(WhatsApp.getInstance(), id);
    }

    public long save() {
        return getManager().save(SearchTable.class);
    }

    public static final SearchEntity create() {
        return Factory.create(WhatsApp.getInstance());
    }

    /**
     *
     */
    static class Factory {

        static final boolean add(Context context, String id) {
            SearchTable ds = RecordManager.getInstance(context).obtainTable(SearchTable.class);
            ds.get(id).ifPresent(e -> ds.remove(e));

            {
                SearchEntry entry = new SearchEntry(id);
                ds.add(0, entry);
            }

            RecordManager.getInstance(context).save(ds);

            return true;
        }

        static final SearchEntity create(Context context) {

            RecordManager mgr = RecordManager.getInstance(context);

            SearchTable ds = mgr.obtainTable(SearchTable.class);
            List<SearchEntity> list = ds.getList().stream()
                    .map(e -> new SearchEntity(context, e))
                    .collect(Collectors.toList());

            SearchEntity entity = new SearchEntity(context, null, list);
            return entity;
        }

    }

}
