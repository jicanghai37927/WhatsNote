package club.andnext.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AudioHelper {

    private static final String TAG = "AudioHelper";

    public static final int TYPE_RECEIVER   = 1;
    public static final int TYPE_SPEAKER    = 2;

    Boolean isSpeakerOn;
    AudioManager audioManager;

    HeadsetPlugReceiver headsetPlugReceiver;
    BiConsumer<AudioHelper, Integer> headsetPlugListener;

    NoisyAudioStreamReceiver noisyAudioStreamReceiver;
    Consumer<AudioHelper> noisyAudioListener;

    FragmentActivity context;

    public AudioHelper(FragmentActivity context) {
        this.context = context;

        this.audioManager = (AudioManager)(context.getSystemService(Context.AUDIO_SERVICE));
        context.getLifecycle().addObserver(new LifecycleListener());
    }

    public void setSpeakerOn(boolean value) {
        this.isSpeakerOn = value;

        int type;
        if (isWiredHeadsetOn()) {
            type = TYPE_SPEAKER;
        } else if (isBluetoothOn()) {
            type = TYPE_SPEAKER;
        } else {
            type = value? TYPE_SPEAKER: TYPE_RECEIVER;
        }

        this.setDestination(type);
    }

    public boolean isSpeakerOn() {
        if (isSpeakerOn == null) {
            return false;
        }

        return this.isSpeakerOn;
    }

    public void setDestination(int type) {

        switch (type) {
            case TYPE_RECEIVER: {
                this.setDestination(AudioManager.MODE_IN_COMMUNICATION, false);
                break;
            }
            case TYPE_SPEAKER: {
                this.setDestination(AudioManager.MODE_NORMAL, true);
                break;
            }
        }
    }

    public void setMode(int mode) {

        // setMode directly
        audioManager.setMode(mode);
    }

    public int getMode() {
        return audioManager.getMode();
    }

    public void setSpeakerphoneOn(boolean value) {

        // setSpeakerphone directly
        audioManager.setSpeakerphoneOn(value);
    }

    public boolean isSpeakerphoneOn() {
        return audioManager.isSpeakerphoneOn();
    }

    public boolean isWiredHeadsetOn() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);

            for (AudioDeviceInfo deviceInfo : audioDevices) {
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                    return true;
                }

            }

            return false;
        } else {
            return audioManager.isWiredHeadsetOn();
        }
    }

    public boolean isBluetoothOn() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);

            for (AudioDeviceInfo deviceInfo : audioDevices) {
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return true;
                }

            }

            return false;
        } else {
            return audioManager.isBluetoothA2dpOn() || audioManager.isBluetoothScoOn();
        }
    }

    public void setOnHeadsetPlugListener(BiConsumer<AudioHelper, Integer> listener) {
        this.headsetPlugListener = listener;
    }

    public void setOnNoisyAudioListener(Consumer<AudioHelper> listener) {
        this.noisyAudioListener = listener;
    }

    void setDestination(int mode, boolean speaker) {

        // set params directly

        // must be first
        audioManager.setMode(mode);

        // and second
        audioManager.setSpeakerphoneOn(speaker);
    }

    void onHeadsetPlugEvent(String name, int state, int microphone) {
        Log.w(TAG, name + ", state = " + state + ", microphone = " + microphone);

        if (isSpeakerOn != null) {
            this.setSpeakerOn(isSpeakerOn);
        }

        if (headsetPlugListener != null) {
            headsetPlugListener.accept(AudioHelper.this, state);
        }
    }

    void onNoisy() {

        if (isSpeakerOn != null) {
            this.setSpeakerOn(isSpeakerOn);
        }

        if (noisyAudioListener != null) {
            noisyAudioListener.accept(AudioHelper.this);
        }
    }

    private void registerHeadsetPlugReceiver(){
        if (headsetPlugReceiver == null) {
            headsetPlugReceiver = new HeadsetPlugReceiver();

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_HEADSET_PLUG);

            context.registerReceiver(headsetPlugReceiver, filter);
        }
    }

    private void unregisterHeadsetPlugReceiver(){
        if (headsetPlugReceiver != null) {

            context.unregisterReceiver(headsetPlugReceiver);

            headsetPlugReceiver = null;
        }
    }

    private void registerNoisyAudioStreamReceiver(){
        if (noisyAudioStreamReceiver == null) {
            this.noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();

            IntentFilter filter = new IntentFilter();
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

            context.registerReceiver(noisyAudioStreamReceiver, filter);
        }
    }

    private void unregisterNoisyAudioStreamReceiver(){
        if (noisyAudioStreamReceiver != null) {

            context.unregisterReceiver(noisyAudioStreamReceiver);

            this.noisyAudioStreamReceiver = null;
        }
    }

    /**
     *
     */
    private class LifecycleListener implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        void onCreate() {

            registerHeadsetPlugReceiver();
            registerNoisyAudioStreamReceiver();

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {

            unregisterHeadsetPlugReceiver();
            unregisterNoisyAudioStreamReceiver();

            context.getLifecycle().removeObserver(this);
        }
    }

    /**
     *
     */
    private class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            name = (name == null)? "": name;

            int state = intent.getIntExtra("state", 0);
            int microphone = intent.getIntExtra("microphone", 0);

            onHeadsetPlugEvent(name, state, microphone);
        }

    }

    /**
     *
     */
    private class NoisyAudioStreamReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = AudioManager.ACTION_AUDIO_BECOMING_NOISY;

            if (action.equals(intent.getAction())) {
                onNoisy();
            }
        }
    }
}
