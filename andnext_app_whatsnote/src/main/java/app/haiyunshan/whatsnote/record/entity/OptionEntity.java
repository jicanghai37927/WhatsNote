package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.OptionTable;
import app.haiyunshan.whatsnote.record.entry.OptionEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionEntity extends AbstractEntitySet<OptionEntity, OptionEntry> {

    public static final String SECTION_ENTRANCE = "entrance";
    public static final String SECTION_FAVORITE = "favorite";
    public static final String SECTION_TAG      = "tag";

    static final String ORDER_ASC   = "asc";
    static final String ORDER_DESC  = "desc";

    SortEntity sort;        //
    SortEntity recentSort;  //

    OptionEntity(Context context, OptionEntry entry) {
        super(context, entry);
    }

    public SortEntity getSort() {
        if (sort == null) {
            Optional<String> id = (entry.getSortId());
            Optional<String> order = (entry.getSortOrder());

            this.sort = SortEntity.create(id.orElse(SortEntity.ID_NAME));
            if (order.orElse(ORDER_ASC).equals(ORDER_DESC)) {
                sort.setReverse(true);
            }
        }

        return new SortEntity(sort);
    }

    public void setSort(SortEntity sort) {

        if ((this.sort != null) && (this.sort.equals(sort))) {
            return;
        }

        {
            this.sort = sort;

            entry.setSortId(sort.getId());
            entry.setSortOrder(sort.isReverse() ? ORDER_DESC : ORDER_ASC);
        }
    }

    public SortEntity getRecentSort() {

        if (recentSort == null) {

            Optional<String> id = entry.getRecentSortId();
            Optional<String> order = entry.getRecentSortOrder();

            this.recentSort = SortEntity.create(id.orElse(SortEntity.ID_MODIFIED));
            if (order.orElse(ORDER_DESC).equals(ORDER_DESC)) {
                recentSort.setReverse(true);
            }
        }

        return new SortEntity(recentSort);
    }

    public void setRecentSort(@NonNull SortEntity sort) {

        if ((this.recentSort != null) && (this.recentSort.equals(sort))) {
            return;
        }

        {
            this.recentSort = sort;

            entry.setRecentSortId(sort.getId());
            entry.setRecentSortOrder(sort.isReverse() ? ORDER_DESC : ORDER_ASC);
        }
    }

    public boolean isSectionExpand(final String section) {
        return entry.getSectionList()
                .map(list -> list.indexOf(section) >= 0)
                .orElse(false);

    }

    public void setSectionExpand(String section, boolean value) {
        List<String> list = entry.getSectionList().orElseGet(() -> {
            ArrayList<String> l = new ArrayList<>();
            entry.setSectionList(l);
            return l;
        });

        list.remove(section);

        if (value) {
            list.add(section);
        }
    }

    public long save() {
        return getManager().save(OptionTable.class);
    }

    public static final OptionEntity obtain() {
        return Factory.obtain(WhatsApp.getInstance());
    }

    /**
     *
     */
    static class Factory {

        public static final OptionEntity obtain(final Context context) {
            final RecordManager mgr = RecordManager.getInstance(context);

            return mgr.obtainEntity(OptionEntity.class, e -> {
                OptionEntity entity = create(context);
                mgr.put(entity);

                return entity;
            });
        }

        static final OptionEntity create(Context context) {
            RecordManager mgr = RecordManager.getInstance(context);
            OptionTable ds = mgr.obtainTable(OptionTable.class);
            if (ds.getList().isEmpty()) {
                OptionEntry entry = new OptionEntry(null);
                entry.setSectionList(new ArrayList<>(Arrays.asList(SECTION_ENTRANCE, SECTION_FAVORITE, SECTION_TAG)));

                ds.add(entry);
            }

            return new OptionEntity(context, ds.get(0));
        }
    }

}
