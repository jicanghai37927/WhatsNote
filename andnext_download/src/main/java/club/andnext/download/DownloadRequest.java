package club.andnext.download;

import club.andnext.utils.UUIDUtils;

import java.io.File;

public class DownloadRequest {

    String id;
    String source;
    File destination;

    DownloadListener listener;

    public DownloadRequest(String source, File destination) {
        this(UUIDUtils.next(), source, destination);
    }

    public DownloadRequest(String id, String source, File destination) {
        this.id = id;
        this.source = source;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public File getDestination() {
        return destination;
    }

    public DownloadRequest widthListener(DownloadListener listener) {
        this.listener = listener;
        return this;
    }
}
