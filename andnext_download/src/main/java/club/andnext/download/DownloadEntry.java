package club.andnext.download;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class DownloadEntry extends BaseEntry {

    @SerializedName("source")
    String source;

    @SerializedName("destination")
    String destination;

    @SerializedName("length")
    long length;

    public DownloadEntry(DownloadRequest request) {
        super(request.getId());

        this.source = request.getSource();
        this.destination = request.getDestination().getAbsolutePath();
        this.length = -1;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

}
