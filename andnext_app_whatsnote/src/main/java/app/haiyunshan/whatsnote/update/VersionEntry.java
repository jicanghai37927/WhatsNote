package app.haiyunshan.whatsnote.update;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class VersionEntry extends BaseEntry {

    @SerializedName("title")
    String title;

    @SerializedName("code")
    int code;

    @SerializedName("checksum")
    String checksum;

    @SerializedName("name")
    String name;

    @SerializedName("developer")
    String developer;

    @SerializedName("size")
    long size;

    @SerializedName("source")
    String source;

    @SerializedName("brief")
    String brief;

    @SerializedName("detail")
    String detail;

    public VersionEntry() {
        super("");
    }

    public String getTitle() {
        return title;
    }

    public int getCode() {
        return code;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getName() {
        return name;
    }

    public String getDeveloper() {
        return developer;
    }

    public long getSize() {
        return size;
    }

    public String getSource() {
        return source == null? "": source;
    }

    public String getBrief() {
        return brief;
    }

    public String getDetail() {
        return detail;
    }
}
