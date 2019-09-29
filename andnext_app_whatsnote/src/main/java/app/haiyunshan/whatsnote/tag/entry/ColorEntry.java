package app.haiyunshan.whatsnote.tag.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class ColorEntry extends BaseEntry {

    @SerializedName("icon")
    String icon;

    @SerializedName("color")
    String color;

    @SerializedName("check")
    String check;

    @SerializedName("bg")
    String background;

    public ColorEntry(String id) {
        super(id);
    }

    public ColorEntry() {
        this("");

        this.icon = "#00000000";
        this.color = "#474747";
        this.check = "#606060";
        this.background = "#e0e0e0";
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public String getCheck() {
        return check;
    }

    public String getBackground() {
        return background;
    }
}
