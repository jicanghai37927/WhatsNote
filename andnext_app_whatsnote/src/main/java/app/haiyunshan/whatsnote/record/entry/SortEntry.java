package app.haiyunshan.whatsnote.record.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class SortEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    @SerializedName("visible")
    boolean visible;

    public SortEntry(String id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }
}
