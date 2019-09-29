package club.andnext.base;

import com.google.gson.annotations.SerializedName;

public class BaseEntry {

    @SerializedName("id")
    String id;

    public BaseEntry(String id) {
        this.id = id;
    }

    public String getId() {
        return id == null? "": id;
    }
}
