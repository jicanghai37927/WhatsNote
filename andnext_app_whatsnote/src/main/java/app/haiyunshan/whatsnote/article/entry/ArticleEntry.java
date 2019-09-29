package app.haiyunshan.whatsnote.article.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

public abstract class ArticleEntry extends BaseEntry {

    @SerializedName("type")
    String type;

    public ArticleEntry(String id, String type) {
        super(id);

        this.type = type;
    }

    public String getType() {
        return type;
    }

}
