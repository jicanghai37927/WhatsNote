package app.haiyunshan.whatsandroid;

import android.media.AudioManager;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.helper.AudioHelper;

public class TestAudioHelperActivity extends AppCompatActivity {

    AudioHelper audioHelper;

    int[] mode = new int[] {
            AudioManager.MODE_IN_COMMUNICATION,
            AudioManager.MODE_NORMAL,
            AudioManager.MODE_RINGTONE
    };
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio_helper);

        this.audioHelper = new AudioHelper(this);
        audioHelper.setOnHeadsetPlugListener(this::onHeadsetPlug);

        this.onToggleModeClick(findViewById(R.id.btn_toggle_mode));
        this.onToggleSpeakerClick(findViewById(R.id.btn_toggle_speaker));

        audioHelper.setOnNoisyAudioListener(this::onNoisy);
    }

    void onReceiverClick(View view) {
        audioHelper.setDestination(AudioHelper.TYPE_RECEIVER);
    }


    void onHeadsetClick(View view) {

    }

    void onSpeakerClick(View view) {
        audioHelper.setDestination(AudioHelper.TYPE_SPEAKER);
    }

    void onToggleModeClick(View view) {
        ++index;
        index %= mode.length;

        audioHelper.setMode(mode[index]);
        int mode = audioHelper.getMode();
        ((TextView)view).setText("模式：" + mode);
    }

    void onToggleSpeakerClick(View view) {
        audioHelper.setSpeakerphoneOn(!audioHelper.isSpeakerphoneOn());

        boolean on = audioHelper.isSpeakerphoneOn();
        ((TextView)view).setText("扬声器：" + on);
    }

    void onDetectHeadsetClick(View view) {
        boolean value = audioHelper.isWiredHeadsetOn();
        ((TextView)view).setText("查询有线耳机状态：" + value);
    }

    void onHeadsetPlug(AudioHelper helper, int state) {
        onDetectHeadsetClick(findViewById(R.id.btn_headset));
    }

    void onNoisy(AudioHelper helper) {
        TextView view = findViewById(R.id.tv_noisy);
        view.setText("Noisy");

        view.postDelayed(() -> view.setText(""), 1000);
    }
}
