package app.haiyunshan.whatsnote.update;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

public class UpdateEntry extends BaseEntry {

    @SerializedName("downloadId")
    String downloadId;

    @SerializedName("downloadChecksum")
    String downloadChecksum;

    @SerializedName("downloadCode")
    int downloadCode;

    @SerializedName("daily")
    String daily;

    @SerializedName("version")
    VersionEntry version;

    public UpdateEntry() {
        super("");

        this.downloadId = "";
        this.downloadCode = -1;
        this.daily = "";
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getDownloadChecksum() {
        return downloadChecksum == null? "": downloadChecksum;
    }

    public void setDownloadChecksum(String downloadChecksum) {
        this.downloadChecksum = downloadChecksum;
    }

    public int getDownloadCode() {
        return downloadCode;
    }

    public void setDownloadCode(int downloadCode) {
        this.downloadCode = downloadCode;
    }

    public String getDaily() {
        return daily;
    }

    public void setDaily(String daily) {
        this.daily = daily;
    }

    public VersionEntry getVersion() {
        return version;
    }

    public void setVersion(VersionEntry version) {
        this.version = version;
    }
}
