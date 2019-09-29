package app.haiyunshan.whatsnote.record.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class TagEntry extends BaseEntry {

    @SerializedName("name")
    String name;

    @SerializedName("color")
    String color;

    public TagEntry(String id, String name, String color) {
        super(id);

        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
