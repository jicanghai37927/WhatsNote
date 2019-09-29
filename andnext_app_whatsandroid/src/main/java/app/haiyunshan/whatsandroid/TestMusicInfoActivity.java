package app.haiyunshan.whatsandroid;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.haiyunshan.whatsandroid.kugou.KugouMusic;
import app.haiyunshan.whatsandroid.netease.NeteaseMusic;
import app.haiyunshan.whatsandroid.remote.TestRemoteManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;


public class TestMusicInfoActivity extends AppCompatActivity {

    TestRemoteManager remoteManager;

    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_music_info);

        remoteManager = TestRemoteManager.getInstance();

        resultView = findViewById(R.id.tv_result);
    }

    void onNeteaseClick(View view) {
        // 473817846 你是我心内的一首歌

        EditText edit = findViewById(R.id.edit_netease);
        String hash = edit.getText().toString().trim();

        String ids = Arrays.toString(new String[]{hash});

        Observable<NeteaseMusic> observable = remoteManager.getService().getNeteaseMusic(ids);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(m ->  {
                    resultView.setText(m.toString());
                });
    }

    void onKugouClick(View view) {
        // ef6fa0690bf825f069e0e7d81ba46201 江智民、周虹 - 你是我心内的一首歌

        EditText edit = findViewById(R.id.edit_kugou);
        String hash = edit.getText().toString().trim();

        Observable<KugouMusic> observable = remoteManager.getService().getKugouMusic(hash);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(m ->  {
                    resultView.setText(m.toString());
                });

    }
}
