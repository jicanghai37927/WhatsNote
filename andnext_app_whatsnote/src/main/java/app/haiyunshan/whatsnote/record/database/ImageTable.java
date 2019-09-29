package app.haiyunshan.whatsnote.record.database;

import app.haiyunshan.whatsnote.record.entry.ImageEntry;
import club.andnext.base.BaseTable;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Optional;

public class ImageTable extends BaseTable<ImageEntry> {

    @SerializedName("no")
    int number;

    public ImageTable() {
        this.number = 0;
    }

    public int getNumber() {
        return this.number;
    }

    public int increase() {
        ++number;

        return number;
    }

    public Optional<ImageEntry> get(String id, String documentId) {

        ImageEntry target = null;

        List<ImageEntry> list = this.getList();
        for (ImageEntry entry: list) {
            if (entry.getId().equals(id) && entry.getDocumentId().equals(documentId)) {
                target = entry;
                break;
            }
        }

        return Optional.ofNullable(target);
    }

    public long removeByDocument(String documentId) {
        long count = 0;

        List<ImageEntry> list = this.getList();
        if (list.isEmpty()) {
            return count;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            ImageEntry e = list.get(i);
            if (e.getDocumentId().equals(documentId)) {
                list.remove(i);

                ++count;
            }
        }

        return count;
    }
}