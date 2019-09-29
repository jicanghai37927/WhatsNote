package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.database.*;
import club.andnext.base.BaseTable;
import club.andnext.utils.GsonUtils;

import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

class RecordManager {

    static final String TAG = "RecordManager";

    HashMap<Class<? extends BaseTable>, String> assetMap;
    HashMap<Class<? extends BaseTable>, File> fileMap;
    HashMap<Class<? extends BaseTable>, BaseTable> tableMap;

    HashMap<Class<? extends AbstractEntitySet>, AbstractEntitySet> entityMap;

    HashMap<String, Comparator<RecordEntity>> comparatorMap;

    Context context;

    static RecordManager instance;

    static final RecordManager getInstance(Context context) {
        if (instance == null) {
            instance = new RecordManager(context);
        }

        return instance;
    }

    private RecordManager(Context context) {

        {
            this.context = context.getApplicationContext();
        }

        {
            this.assetMap = new HashMap<>();

            assetMap.put(TagTable.class, "record/tag_ds.json");
            assetMap.put(SortTable.class, "record/sort_ds.json");
            assetMap.put(EntranceTable.class, "record/entrance_ds.json");
        }

        {
            this.fileMap = new HashMap<>();

            File dir = Directory.get(context, Directory.DIR_NOTE);

            {
                fileMap.put(RecordTable.class, new File(dir, "record_ds.json"));

                fileMap.put(TagTable.class,         new File(dir, "tag_ds.json"));
                fileMap.put(FavoriteTable.class,    new File(dir, "favorite_ds.json"));
                fileMap.put(RecentTable.class, new File(dir, "recent_ds.json"));
//              fileMap.put(SearchTable.class,      new File(dir, "search_ds.json"));
                fileMap.put(SavedStateTable.class, new File(dir, "saved_state_ds.json"));

                fileMap.put(OptionTable.class, new File(dir, "option_ds.json"));
            }

            {
                fileMap.put(ImageTable.class, new File(dir, "image_ds.json"));
            }
        }

        {
            this.tableMap = new HashMap<>();
        }

        {
            this.entityMap = new HashMap<>();
        }

    }

    void put(@NonNull AbstractEntitySet entity) {
        entityMap.put(entity.getClass(), entity);
    }

    <V extends AbstractEntitySet> V obtainEntity(Class<V> kind, Function<? super Class<? extends AbstractEntitySet>, ? extends AbstractEntitySet> mappingFunction) {
        return (V)entityMap.computeIfAbsent(kind, mappingFunction);
    }

    Comparator<RecordEntity> obtainComparator(String id) {

        if (comparatorMap == null) {

            this.comparatorMap = new HashMap<>();

            comparatorMap.put(SortEntity.ID_NAME,
                    Comparator.comparing(RecordEntity::getName, Collator.getInstance()::compare));

            comparatorMap.put(SortEntity.ID_CREATED,
                    Comparator.comparing(RecordEntity::getCreated));

            comparatorMap.put(SortEntity.ID_MODIFIED,
                    Comparator.comparing(RecordEntity::getModified));

            comparatorMap.put(SortEntity.ID_SIZE,
                    Comparator.comparing(RecordEntity::getSize)
                            .thenComparing(RecordEntity::getName, Collator.getInstance()::compare));

            comparatorMap.put(SortEntity.ID_TAG,
                    new TagComparator()
                            .thenComparing(RecordEntity::getName, Collator.getInstance()::compare));

        }

        return comparatorMap.getOrDefault(id, comparatorMap.get(SortEntity.ID_NAME));
    }

    final <T extends BaseTable> T obtainTable(Class<T> kind) {
        T ds = (T) tableMap.get(kind);
        if (ds != null) {
            return ds;
        }

        ds = createTable(kind);
        tableMap.put(kind, ds);

        return ds;
    }

    final File getSnapshot(String id) {
        File file = Directory.get(context, Directory.DIR_NOTE);
        file = new File(file, "snapshot");
        file.mkdirs();

        file = new File(file, id + ".snap");
        return file;
    }

    long save() {
        Consumer<BaseTable> f = e -> {
            File file = fileMap.get(e.getClass());
            if (file != null) {
                GsonUtils.toJson(e, file);
            }
        };

        tableMap.values().stream().forEach(f);

        return tableMap.size();
    }

    long save(Class<? extends BaseTable>... kinds) {
        Consumer<Class<? extends  BaseTable>> f = c -> {
            BaseTable e = tableMap.get(c);
            File file = fileMap.get(c);
            if (e != null && file != null) {
                GsonUtils.toJson(e, file);
            }
        };

        Arrays.stream(kinds).forEach(f);

        return kinds.length;
    }

    long save(BaseTable... tables) {
        Consumer<BaseTable> f = e -> {
            File file = fileMap.get(e.getClass());
            if (file != null) {
                GsonUtils.toJson(e, file);
            }
        };

        Arrays.stream(tables).forEach(f);

        return tables.length;
    }

    private <T> T createTable(Class<T> kind) {
        T obj = null;

        File file = fileMap.get(kind);
        if (file != null && file.exists()) {
            obj = GsonUtils.fromJson(file, kind);
        }

        if (obj == null) {
            obj = fromAsset(kind);
        }

        if (obj == null) {
            try {
                obj = kind.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    private <T> T fromAsset(Class<T> kind) {
        T obj = null;

        String path = assetMap.get(kind);
        if (TextUtils.isEmpty(path)) {
            return obj;
        }

        obj = GsonUtils.fromJson(context, path, kind);
        return obj;
    }

    /**
     *
     */
    static class TagComparator implements Comparator<RecordEntity> {

        TagEntity tagEntity;

        public TagComparator() {
            tagEntity = TagEntity.obtain();
        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {

            List<String> list1 = o1.getTagList();
            List<String> list2 = o2.getTagList();

            int result = 0;

            if (list1.isEmpty() && list2.isEmpty()) {

            } else if (list1.isEmpty() && !list2.isEmpty()) {
                result = -1;
            } else if (!list1.isEmpty() && list2.isEmpty()) {
                result = 1;
            } else {
                int size1 = list1.size();
                int size2 = list2.size();

                int size = Math.min(size1, size2);
                for (int i = 0; i < size; i++) {
                    String tag1 = list1.get(i);
                    String tag2 = list2.get(i);

                    int a1 = tagEntity.indexOf(tag1);
                    a1 = (a1 < 0)? Integer.MAX_VALUE: a1;
                    int a2 = tagEntity.indexOf(tag2);
                    a2 = (a2 < 0)? Integer.MAX_VALUE: a2;

                    if (a1 != a2) {
                        result = (a1 > a2)? 1: -1;
                        break;
                    }
                }

                if (result == 0) {
                    if (size1 != size2) {
                        result = (size1 > size2)? 1: -1;
                    }
                }
            }

            return result;
        }
    }

}
