package app.haiyunshan.whatsandroid;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.haiyunshan.whatsandroid.remote.TestRemoteManager;
import okhttp3.*;

import java.io.IOException;

public class TestNeteaseLyricActivity extends AppCompatActivity {

    EditText musicEdit;
    TextView resultView;

    TestRemoteManager remoteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_netease_lyric);

        this.musicEdit = findViewById(R.id.edit_music);
        this.resultView = findViewById(R.id.tv_result);

        this.remoteManager = TestRemoteManager.getInstance();
    }

    void onNeteaseClick(View view) {

      final String ROOT_URL = "https://music.163.com/m/song?id=";
//    final String ROOT_URL = "https://music.163.com/song?id=";

        String url = ROOT_URL;
        url += musicEdit.getText().toString();


        fetch(view, url);
    }

    void onKugouClick(View view) {

        // C872515B7446E06ADF167474DBEA8C07 世界第一等

        // http://m.kugou.com/kgsong/C872515B7446E06ADF167474DBEA8C07.html

        // https://www.kugou.com/song/#hash=C872515B7446E06ADF167474DBEA8C07
//        final String ROOT_URL = "https://www.kugou.com/song/#hash=";
        final String ROOT_URL = "http://www.kugou.com/song/#hash=";

        String url = ROOT_URL;
        url += musicEdit.getText().toString().toUpperCase();


        fetch(view, url);
    }

    void fetch(View view, String url) {
        view.setEnabled(false);

        OkHttpClient client = remoteManager.getHttpClient();

        final String location = url;

        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String str = e.toString();
                setResult(view, str);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] data = response.body().bytes();
                String str = new String(data, "utf-8");
                str = data.length + "\n\n" + str;
                str = location + "\n\n" + str;

                setResult(view, str);
            }
        });
    }

    void setResult(View button, String text) {
        resultView.post(() -> {
            button.setEnabled(true);

            resultView.setText(text);
        });
    }
}
