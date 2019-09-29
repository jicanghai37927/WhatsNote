package club.andnext.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class AudioPlayer {

    private int duration;
    private boolean isPlaying = false;

    boolean isLooping = false;

    private File targetFile;
    private Uri targetUri;
    private MediaPlayer mMediaPlayer = null;
    private Consumer<AudioPlayer> mOnCompletionListener;

    Context context;

    public AudioPlayer() {
        this(null);
    }

    public AudioPlayer(Context context) {
        this.context = context;
    }

    public void setTarget(File file) {
        this.targetFile = file;
        this.targetUri = null;
    }

    public void setTarget(Uri uri) {
        this.targetFile = null;
        this.targetUri = uri;
    }

    public void setLooping(boolean value) {
        this.isLooping = value;
    }

    public void setOnCompletionListener(Consumer<AudioPlayer> consumer) {
        this.mOnCompletionListener = consumer;
    }

    public boolean start() {
        this.stop();

        if (targetFile == null && targetUri == null) {
            return false;
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(isLooping);

        try {
            if (targetFile != null) {
                mMediaPlayer.setDataSource(targetFile.getAbsolutePath());
            }
            if (targetUri != null) {
                mMediaPlayer.setDataSource(context, targetUri);
            }

            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(this::onCompletion);
            mMediaPlayer.setOnPreparedListener(mp -> mMediaPlayer.start());

            this.duration = mMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();

            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        boolean result = (mMediaPlayer != null);
        this.isPlaying = result;

        return result;
    }

    public void stop() {
        this.isPlaying = false;
        this.duration = 0;

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            this.isPlaying = true;

            mMediaPlayer.start();
        }
    }

    public void pause() {
        this.isPlaying = false;

        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public boolean isRunning() {
        return (mMediaPlayer != null);
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public int getDuration() {
        if (mMediaPlayer == null) {
            return this.duration;
        }

        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        if (mMediaPlayer == null) {
            return 0;
        }

        return mMediaPlayer.getCurrentPosition();
    }

    public MediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }

    void onCompletion(MediaPlayer mp) {
        this.stop();

        if (mOnCompletionListener != null) {
            mOnCompletionListener.accept(this);
        }
    }

}
