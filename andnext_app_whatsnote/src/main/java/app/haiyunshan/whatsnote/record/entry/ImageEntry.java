package app.haiyunshan.whatsnote.record.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class ImageEntry extends BaseEntry {

    @SerializedName("no")
    int number;

    @SerializedName("doc")
    String documentId;

    public ImageEntry(String id, int number, String documentId) {
        super(id);

        this.number = number;
        this.documentId = documentId;
    }

    public int getNumber() {
        return number;
    }

    public String getDocumentId() {
        return documentId;
    }
}
