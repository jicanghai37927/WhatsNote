package app.haiyunshan.whatsandroid;

import android.os.Environment;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.haiyunshan.whatsandroid.record.AudioRecordDemo;
import app.haiyunshan.whatsandroid.record.MediaRecorderDemo;
import app.haiyunshan.widget.WaveView;

import java.io.File;

public class TestVolumeActivity extends AppCompatActivity {

    MediaRecorderDemo mediaRecorderDemo;
    AudioRecordDemo audioRecordDemo;

    WaveView waveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_volume);

        this.waveView = findViewById(R.id.wave_view);
    }

    void onMediaClick(View view) {

        onStopClick(view);

        this.waveView.clear();

        {
            File file = new File(Environment.getExternalStorageDirectory(), "media_record_volume.aac");
            MediaRecorderDemo demo = new MediaRecorderDemo(this::onTapeUpdate, file);
            demo.startRecord();

            this.mediaRecorderDemo = demo;
        }

        {
//            this.audioRecordDemo = new AudioRecordDemo();
//            audioRecordDemo.getNoiseLevel();
        }
    }

    void onAudioClick(View view) {
        onStopClick(view);

        this.waveView.clear();

        {
            this.audioRecordDemo = new AudioRecordDemo(this::onTapeUpdate);
            audioRecordDemo.getNoiseLevel();
        }

        {
//            File file = new File(Environment.getExternalStorageDirectory(), "media_record_volume.aac");
//            MediaRecorderDemo demo = new MediaRecorderDemo(file);
//            demo.startRecord();
//
//            this.mediaRecorderDemo = demo;
        }
    }

    void onStopClick(View view) {
        if (mediaRecorderDemo != null) {
            mediaRecorderDemo.stopRecord();
            mediaRecorderDemo = null;
        }

        if (audioRecordDemo != null) {
            audioRecordDemo.stop();
            audioRecordDemo = null;
        }
    }

    void onTapeUpdate(double value) {
        waveView.addDecibel((int)value);
    }
}
