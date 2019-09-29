package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.entry.SearchEntry;

import java.util.List;
import java.util.stream.Collectors;

public class SearchRecordSet extends AbstractEntitySet<RecordEntity, SearchEntry> {

    String keyword;

    SearchRecordSet(Context context, String keyword, List<RecordEntity> list) {
        super(context, null, list);

        this.keyword = (keyword == null)? "": keyword;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public long save() {
        return getManager().save();
    }

    public static final SearchRecordSet create(String keyword) {
        return Factory.create(WhatsApp.getInstance(), keyword);
    }

    /**
     *
     */
    static class Factory {

        public static final SearchRecordSet create(Context context, String keyword) {
            if (TextUtils.isEmpty(keyword)) {
                return new SearchRecordSet(context, "", null);
            }

            final String lowerKeyword = keyword.toLowerCase();
            RecordManager mgr = RecordManager.getInstance(context);

            RecordTable ds = mgr.obtainTable(RecordTable.class);
            List<RecordEntity> list = ds.getList().stream()
                    .filter(e -> {
                        String name = RecordUtils.getName(e).toLowerCase();
                        return (name.indexOf(lowerKeyword) >= 0);
                    })
                    .map(e -> new RecordEntity(context, e.getId(), e))
                    .collect(Collectors.toList());

            SearchRecordSet rs = new SearchRecordSet(context, keyword, list);
            return rs;
        }

    }
}
