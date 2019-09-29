package club.andnext.download;

public interface DownloadListener {

    int STATUS_PENDING = 1 << 0;
    int STATUS_RUNNING = 1 << 1;
    int STATUS_PAUSED = 1 << 2;
    int STATUS_SUCCESSFUL = 1 << 3;
    int STATUS_FAILED = 1 << 4;

    void onStatusChanged(DownloadEntity entity, int status);

    void onProgressUpdate(DownloadEntity entity, long progress, long max, long speed);

}
