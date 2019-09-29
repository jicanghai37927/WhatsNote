package app.haiyunshan.whatsandroid;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.telephony.mbms.DownloadRequest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class TestDownloadManagerActivity extends AppCompatActivity {

    String source = "http://andnext.club/idiom/release/apk/cycd_8.08.2.apk";

    View btnDownload;
    TextView statusView;

    long downloadId;
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_download_manager);

        source = "https://raw.githubusercontent.com/luckybilly/Gloading/master/image/en_load_empty_data.gif";

        this.downloadManager = (DownloadManager)(getSystemService(Activity.DOWNLOAD_SERVICE));

        this.btnDownload = findViewById(R.id.btn_download);
        this.statusView = findViewById(R.id.tv_status);
        btnDownload.setOnClickListener(this::onDownloadClick);
    }

    void onDownloadClick(View view) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = downloadManager.query(query);
        boolean result = (cursor.getCount() > 0);
        cursor.close();

        if (result) {
            downloadManager.remove(downloadId);
        }

        this.downloadId = test();

        btnDownload.setEnabled(false);
    }

    long start() {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source));

        request.setVisibleInDownloadsUi(true);
//        request.setMimeType("application/vnd.android.package-archive");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setAllowedOverRoaming(true);

        {
            File file = getExternalFilesDir(null);
            file = new File(file, "update/download");
            file.mkdirs();
            file = new File(file, "test.apk.whatsdownload");
            request.setDestinationUri(Uri.fromFile(file));
        }

        request.setTitle("测试下载");
        request.setDescription("测试下载");

        long id = downloadManager.enqueue(request);
        Log.w("AA", "id = " + id);
        return id;
    }

    long test() {
        String nameOfFile = "测试";
        String url = "https://raw.githubusercontent.com/luckybilly/Gloading/master/image/en_load_empty_data.gif";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading...");
        request.setTitle(nameOfFile);
        request.setMimeType("image/gif");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/CPG", nameOfFile);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long queueId = manager.enqueue(request);
        return queueId;
    }
}
