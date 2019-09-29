package app.haiyunshan.whatsandroid;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestRecordSoundActivity extends AppCompatActivity {

    private final static int REQUEST_RECORDER = 100;
    private Uri uri;

    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_record_sound);

        this.resultView = findViewById(R.id.tv_result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String hint;

        if (resultCode == RESULT_OK && REQUEST_RECORDER == requestCode){
            uri = data.getData();

            hint = uri.toString();
            resultView.setText(hint);
        }


    }

    void onRecordClick(View view) {
        recorder_Intent();
    }

    public void recorder_Intent(){
        resultView.setText("");

        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

        try {
            startActivityForResult(intent, REQUEST_RECORDER);
        } catch (Exception e) {
            resultView.setText(e.toString());
        }


    }
}
