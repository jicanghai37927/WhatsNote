package app.haiyunshan.whatsandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import club.andnext.helper.AudioPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TestMusicActivity extends AppCompatActivity {

    TextView resultView;

    Uri target;

    AudioManager mAudioManager;
    AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_music);
        this.resultView = findViewById(R.id.tv_result);

        {
            mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        }

        {
            audioPlayer = new AudioPlayer(this);
        }

        {
            String str = getTargetFromPref();
            if (!TextUtils.isEmpty(str)) {
                this.target = Uri.parse(str);
                this.setResult(target);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                this.setResult(uri);
                this.target = uri;

                setTargetToPref(uri.toString());
            }


        }
    }

    void onRequestClick(View view) {
        mAudioManager.abandonAudioFocus(audioFocusChangeListener);

        if (target == null) {
            return;
        }

        if (audioPlayer.isRunning() && audioPlayer.isPlaying()) {
            audioPlayer.pause();
        } else {
            int result = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Log.w("AA", "requestAudioFocus = " + result);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                if (!audioPlayer.isRunning()) {
                    audioPlayer.start();
                } else {
                    if (!audioPlayer.isPlaying()) {
                        audioPlayer.resume();
                    }
                }

            }

        }
    }

    void onAbandonClick(View view) {
        mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        if (target != null) {
            audioPlayer.stop();
        }
    }

    void onPickClick(View view) {
        testPick(this, null,
                "audio/*", null,
                true, 100);
    }

    void onPlayClick(View view) {
        if (target == null) {
            return;
        }

        Context context = this;

        Uri uri = target;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //注意加上这句话

        try {
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    void setResult(Uri uri) {
        audioPlayer.setTarget(uri);

        StringBuilder sb = new StringBuilder();

        sb.append(uri.toString());
        sb.append("\n\n");

        sb.append(getInfo(uri));

        resultView.setText(sb);
    }

    CharSequence getInfo(Uri uri) {
        StringBuilder sb = new StringBuilder();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);

        ArrayList<Pair<Integer, String>> list = new ArrayList<>();

        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_TITLE, "title"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_ARTIST, "artist"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_ALBUM, "album"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_AUTHOR, "author"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_COMPOSER, "composer"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_DATE, "date"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_DURATION, "duration"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE, "hasImage"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO, "hasAudio"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO, "hasVideo"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_WRITER, "writer"));
        list.add(new Pair<>(MediaMetadataRetriever.METADATA_KEY_MIMETYPE, "mimeType"));

        for (Pair<Integer, String> p: list) {
            String value = retriever.extractMetadata(p.first);
            sb.append(p.second + " = " + value);
            sb.append('\n');
        }

        sb.append("\n");

        for (int i = MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER;
             i <= MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT;
             i++) {
            String value = retriever.extractMetadata(i);
            sb.append(i + " = " + value);
            sb.append('\n');
        }

        byte[] embedded = retriever.getEmbeddedPicture();
        sb.append("embedded = " + ((embedded == null)? 0: (embedded.length)));

        retriever.release();

        return sb;
    }

    static final boolean testPick(Activity context, Fragment fragment,
                                         String type, String[] mimeTypes,
                                         boolean documentOnly, int requestCode) {

        boolean result = false;

        if (context == null && fragment == null) {
            return false;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(Intent.ACTION_OPEN_DOCUMENT); // 红米6Pro不支持视频，只能以文件方式选择视频
        list.add(Intent.ACTION_GET_CONTENT); // 坚果手机2会使用SmartianOS文件浏览器，而不是Android的文件选择器

        if (!documentOnly) {
            list.add(0, Intent.ACTION_PICK);
        }

        for (String action : list) {
            if (action.equalsIgnoreCase(Intent.ACTION_PICK)) {
                if (mimeTypes != null && mimeTypes.length > 1) {
                    continue;
                }
            }

            Intent intent = new Intent();

            intent.setAction(action);
            intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, type);

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);

            if (mimeTypes != null && mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            if (action.equalsIgnoreCase(Intent.ACTION_OPEN_DOCUMENT)
                    || action.equalsIgnoreCase(Intent.ACTION_GET_CONTENT)) {
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }

            try {

                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode);
                } else {
                    context.startActivityForResult(intent, requestCode);
                }

                result = true;

                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    String getTargetFromPref() {
        SharedPreferences pref = this.getSharedPreferences("music", Context.MODE_PRIVATE);
        String uri = pref.getString("uri", "");
        if (TextUtils.isEmpty(uri)) {
            return uri;
        }

        try {
            InputStream is = this.getContentResolver().openInputStream(Uri.parse(uri));
            is.close();
        }  catch (Exception e) {
            e.printStackTrace();

            uri = "";
        }

        return uri;
    }

    void setTargetToPref(String uri) {
        SharedPreferences pref = this.getSharedPreferences("music", Context.MODE_PRIVATE);
        pref.edit().putString("uri", uri).commit();
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = (focusChange) -> {
        Log.w("AA", "focusChange = " + focusChange);

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN: {
                audioPlayer.resume();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                audioPlayer.pause();
                break;
            }
        }
    };

}
