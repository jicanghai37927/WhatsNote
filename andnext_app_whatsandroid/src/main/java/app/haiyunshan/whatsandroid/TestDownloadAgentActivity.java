package app.haiyunshan.whatsandroid;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.haiyunshan.whatsandroid.remote.TestRemoteManager;
import club.andnext.download.DownloadAgent;
import club.andnext.download.DownloadEntity;
import club.andnext.download.DownloadListener;
import club.andnext.download.DownloadRequest;

import java.io.File;

public class TestDownloadAgentActivity extends AppCompatActivity {

    Button btnDownload;
    TextView statusView;

    DownloadAgent downloadAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_download_agent);

        TestRemoteManager remoteManager = TestRemoteManager.getInstance();
        downloadAgent = remoteManager.getDownloadAgent();

        this.btnDownload = findViewById(R.id.btn_download);
        this.statusView = findViewById(R.id.tv_status);
        btnDownload.setOnClickListener(this::onDownloadClick);
    }

    void onDownloadClick(View view) {

//        this.start();

        this.test();

        btnDownload.setEnabled(false);
    }

    String start() {
        String source = "http://andnext.club/idiom/release/apk/cycd_8.08.2.apk";

        DownloadEntity entity = downloadAgent.findBySource(source);
        if (entity != null) {
            entity.registerObserver(new FileDownloadListener());

            downloadAgent.start(entity);
        } else {
            File file = getExternalFilesDir(null);
            file = new File(file, "update/download");
            file.mkdirs();
            file = new File(file, "test.apk");

            entity = downloadAgent.enqueue(new DownloadRequest(source, file).widthListener(new FileDownloadListener()));
            downloadAgent.save();
        }

        return entity.getId();
    }

    String test() {
        String source = "https://raw.githubusercontent.com/luckybilly/Gloading/master/image/en_load_empty_data.gif";

        DownloadEntity entity = downloadAgent.findBySource(source);
        if (entity != null) {
            entity.registerObserver(new FileDownloadListener());

            downloadAgent.start(entity);
        } else {
            File file = getExternalFilesDir(null);
            file = new File(file, "update/download");
            file.mkdirs();
            file = new File(file, "en_load_empty_data.gif");

            entity = downloadAgent.enqueue(new DownloadRequest(source, file).widthListener(new FileDownloadListener()));
            downloadAgent.save();
        }

        return entity.getId();
    }

    /**
     *
     */
    class FileDownloadListener implements DownloadListener {

        @Override
        public void onStatusChanged(DownloadEntity entity, int status) {
            btnDownload.setText("" + status);
        }

        @Override
        public void onProgressUpdate(DownloadEntity entity, long progress, long max, long speed) {
            statusView.setText(progress + "/" + max + "|" + speed);
        }
    }
}
