package app.haiyunshan.whatsnote.record.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class EntranceEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    public EntranceEntry(String id) {
        super(id);
    }

    public String getName() {
        return name;
    }

}
