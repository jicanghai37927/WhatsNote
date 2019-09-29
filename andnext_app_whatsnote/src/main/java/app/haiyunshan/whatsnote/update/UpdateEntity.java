package app.haiyunshan.whatsnote.update;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.remote.RemoteManager;
import club.andnext.base.BaseEntity;
import club.andnext.download.DownloadAgent;
import club.andnext.download.DownloadEntity;
import club.andnext.download.DownloadRequest;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.PackageUtils;
import club.andnext.utils.UUIDUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.Optional;

public class UpdateEntity extends BaseEntity<UpdateEntry> {

    static UpdateEntity instance;

    DateTime daily;

    public UpdateEntity(@NonNull Context context, @NonNull UpdateEntry entry) {
        super(context, entry);

        if (entry != null) {
            String date = entry.getDaily();
            if (!TextUtils.isEmpty(date)) {
                this.daily = new DateTime(date);
            }
        }
    }

    public void setVersion(VersionEntry version) {
        {
            this.entry.setVersion(version);
        }

        {
            String downloadId = this.entry.getDownloadId();
            if (TextUtils.isEmpty(downloadId)) {
                return;
            }

            DownloadAgent downloadAgent = RemoteManager.getInstance().getDownloadAgent();
            DownloadEntity downloadEntity = downloadAgent.get(downloadId).orElse(null);
            if (downloadEntity == null) {
                return;
            }

            // if already downloaded, delete downloaded apk file.
            if (((entry.getDownloadCode()) != version.getCode())
                    || (!entry.getDownloadChecksum().equals(version.getChecksum()))
                    || (!downloadEntity.getSource().equals(version.getSource()))) {

                downloadAgent.delete(downloadId, true);
                downloadAgent.save();

                this.entry.setDownloadCode(-1);
                this.entry.setDownloadId("");
                this.entry.setDownloadChecksum("");
            }
        }

    }

    public boolean exist() {
        int now = PackageUtils.getVersionCode(context);
        int update = this.getCode();

        return update > now;
    }

    public Optional<DateTime> getDaily() {
        return Optional.ofNullable(this.daily);
    }

    public void setDaily(DateTime daily) {
        this.daily = daily;

        entry.setDaily(daily.toString("yyyy-MM-dd"));

        this.save();
    }

    public String getTitle() {
        return (getVersion() == null)? "": getVersion().getTitle();
    }

    public int getCode() {
        return (getVersion() == null)? 0: getVersion().getCode();
    }

    public String getChecksum() {
        return (getVersion() == null)? "": getVersion().getChecksum();
    }

    public String getName() {
        return (getVersion() == null)? "": getVersion().getName();
    }

    public String getDeveloper() {
        return (getVersion() == null)? "Amoy Inc.": getVersion().getDeveloper();
    }

    public long getSize() {
        return (getVersion() == null)? 0: getVersion().getSize();
    }

    public String getBrief() {
        return (getVersion() == null)? "": getVersion().getBrief();
    }

    public String getDetail() {
        return (getVersion() == null)? "": getVersion().getDetail();
    }

    public void save() {
        Factory.save(this);
    }

    public DownloadEntity getDownload() {

        String downloadId = this.entry.getDownloadId();
        if (!TextUtils.isEmpty(downloadId)) {
            DownloadAgent downloadAgent = RemoteManager.getInstance().getDownloadAgent();
            DownloadEntity downloadEntity = downloadAgent.get(downloadId).orElse(null);
            return downloadEntity;
        }

        return null;
    }

    public DownloadEntity download() {
        DownloadAgent downloadAgent = RemoteManager.getInstance().getDownloadAgent();

        DownloadEntity entity = this.getDownload();
        if (entity != null) {
            downloadAgent.start(entity);
        } else if (exist()) {
            String id = UUIDUtils.next();
            String source = getVersion().getSource();
            File destination = this.getDestination();

            {
                DownloadRequest request = new DownloadRequest(id, source, destination);
                entity = downloadAgent.enqueue(request);
                downloadAgent.save();
            }

            {
                this.entry.setDownloadId(id);
                this.entry.setDownloadCode(this.getCode());
                this.entry.setDownloadChecksum(this.getChecksum());
                this.save();
            }
        }

        return entity;
    }

    public void deleteDownload() {

        String downloadId = this.entry.getDownloadId();
        if (!TextUtils.isEmpty(downloadId)) {
            DownloadAgent downloadAgent = RemoteManager.getInstance().getDownloadAgent();
            downloadAgent.delete(downloadId, true);
            downloadAgent.save();
        }

        if (!TextUtils.isEmpty(downloadId)) {
            this.entry.setDownloadCode(-1);
            this.entry.setDownloadId("");
            this.entry.setDownloadChecksum("");

            this.save();
        }
    }

    public File getDestination() {
        File file = Factory.getDir(context);
        file = new File(file, getChecksum() + "." + getCode());

        return file;
    }

    VersionEntry getVersion() {
        return entry.getVersion();
    }

    public static final UpdateEntity obtain() {
        if (instance == null) {
            instance = Factory.create(WhatsApp.getInstance());
        }

        return instance;
    }

    /**
     *
     */
    private static class Factory {

        static UpdateEntity create(Context context) {
            File file = getFile(context);
            UpdateEntry entry = GsonUtils.fromJson(file, UpdateEntry.class);
            if (entry == null) {
                entry = new UpdateEntry();
            }

            return new UpdateEntity(context, entry);
        }

        static void save(UpdateEntity entity) {
            File file = getFile(entity.getContext());
            GsonUtils.toJson(entity.entry, file);
        }

        static File getFile(Context context) {
            File file = getDir(context);
            file = new File(file, "update.json");
            return file;
        }

        static File getDir(Context context) {
            File file = context.getExternalFilesDir("update");
            return file;
        }
    }
}
