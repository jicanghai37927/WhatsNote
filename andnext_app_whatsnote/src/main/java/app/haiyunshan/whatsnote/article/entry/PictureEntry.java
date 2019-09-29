package app.haiyunshan.whatsnote.article.entry;

import android.net.Uri;
import com.google.gson.annotations.SerializedName;

public class PictureEntry extends ParagraphEntry {

    public static final String TYPE = "picture";

    @SerializedName("uri")
    String uri; // source uri

    @SerializedName("width")
    int width;

    @SerializedName("height")
    int height;

    @SerializedName("signature")
    String signature; // use for cache bitmap

    public PictureEntry(String id, Uri uri, int width, int height) {
        super(id);
        this.type = TYPE;

        this.uri = (uri == null)? "": uri.toString();
        this.width = width;
        this.height = height;

        this.signature = String.valueOf(this.uri.hashCode());
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSignature() {
        return signature == null? "": signature;
    }

    public String getUri() {
        return uri == null? "": uri;
    }

    public void setUri(Uri uri) {
        if (uri != null) {
            this.uri = uri.toString();
            this.signature = String.valueOf(uri.hashCode());
        } else {
            this.uri = null;
            this.signature = null;
        }
    }
}
