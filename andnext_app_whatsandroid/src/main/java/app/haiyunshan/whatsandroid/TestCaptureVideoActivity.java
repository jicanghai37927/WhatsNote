package app.haiyunshan.whatsandroid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.utils.FileUtils;
import club.andnext.utils.UriUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;

public class TestCaptureVideoActivity extends AppCompatActivity {

    static final int REQUEST_CAPTURE_VIDEO = 1001;

    View videoLayout;
    ImageView thumbView;
    VideoView videoView;

    TextView infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_capture_video);

        this.videoLayout = findViewById(R.id.video_layout);
        this.thumbView = findViewById(R.id.iv_thumb);
        this.videoView = findViewById(R.id.video_view);

        videoView.setOnCompletionListener(this::onCompletion);
        videoView.setOnPreparedListener(this::onPrepared);
        videoView.setOnInfoListener(this::onInfo);
//        MediaController controller = new MediaController(this);
//        videoView.setMediaController(controller);

        this.prepareVideo(getFile());

        this.infoView = findViewById(R.id.tv_info);
        infoView.setText(getInfo(getFile()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAPTURE_VIDEO) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Log.w("AA", "uri = " + uri);
                Log.w("AA", "video duration = " + getDuration(uri));

                printInfo(uri);

                prepareVideo(getFile());
                infoView.setText(getInfo(getFile()));
            }
        }
    }

    void onCompletion(MediaPlayer player) {
        thumbView.setVisibility(View.VISIBLE);

        videoView.stopPlayback();
        videoView.resume();
        videoView.seekTo(0);
    }

    void onPrepared(MediaPlayer player) {

    }

    boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            thumbView.setVisibility(View.INVISIBLE);
        }

        Log.w("AA", "onInfo = " + what + ", extra = " + extra);
        return false;
    }

    void onPlayClick(View view) {
        if (!videoView.isPlaying()) {
            videoView.start();
        }

    }

    void onPauseClick(View view) {
        if (videoView.isPlaying()) {
            videoView.pause();
        }

    }

    void onCaptureClick(View view) {
        this.start();
    }

    void prepareVideo(File file) {
        Point size = getSize(file);
        if (size.equals(0, 0)) {
            videoView.stopPlayback();

            return;
        }

        {
            int width = this.getResources().getDisplayMetrics().widthPixels;
            width = width * 2 / 3;

            int height = size.y * width / size.x;

            ViewGroup.LayoutParams params = videoLayout.getLayoutParams();
            params.width = width;
            params.height = height;

            videoLayout.requestLayout();
        }

        {
            videoView.setVideoPath(file.getAbsolutePath());
        }

        {
            thumbView.setVisibility(View.VISIBLE);

            RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE);
            options.frame(0);

            Glide.with(thumbView)
                    .load(file)
                    .apply(options)
                    .into(thumbView);

        }
    }

    void start() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        File file = this.getFile();
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = UriUtils.fromFile(this, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 24 * 60 * 60); // int value
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10 * 1024 * 1024 * 1024L); // long value

        intent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);

        startActivityForResult(intent, REQUEST_CAPTURE_VIDEO);
    }

    void printInfo(Uri uri) {
        Context context = this;
        Cursor cursor = null;

        try {
            Uri selectedVideo = uri;
            cursor = context.getContentResolver().query(selectedVideo,
                    null, null, null, null);

            if (cursor == null || cursor.getCount() == 0 || cursor.getColumnCount() == 0) {
                Log.w("AA", "找不到记录");
            } else {
                cursor.moveToFirst();
                printInfo(cursor);
            }
        } catch (Exception e) {
            Log.w("AA", "printInfo = " + e.toString());

            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();                   // 关闭cursor
        }


    }

    void printInfo(Cursor cursor) {
        // 视频ID:MediaStore.Audio.Media._ID
        try {
            int index = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            if (index >= 0) {
                int videoId = cursor.getInt(index);
                Log.w("AA", "videoId = " + videoId);
            }

        } finally {

        }

        // 视频名称：MediaStore.Audio.Media.TITLE
        try {
            int index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            if (index >= 0) {
                String title = cursor.getString(index);
                Log.w("AA", "title = " + title);
            }
        } finally {

        }

        // 视频路径：MediaStore.Audio.Media.DATA
        try {
            int index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            if (index >= 0) {
                String videoPath = cursor.getString(index);
                Log.w("AA", "videoPath = " + videoPath);
            }
        } finally {

        }

        // 视频时长：MediaStore.Audio.Media.DURATION
        try {
            int index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            if (index >= 0) {
                int duration = cursor.getInt(index);
                Log.w("AA", "duration = " + duration);
            }
        } finally {

        }

        // 视频大小：MediaStore.Audio.Media.SIZE
        try {
            int index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            if (index >= 0) {
                long size = cursor.getLong(index);
                Log.w("AA", "size = " + size);
            }
        } finally {

        }

        // 视频缩略图路径：MediaStore.Images.Media.DATA
        try {
            int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (index >= 0) {
                String imagePath = cursor.getString(index);
                Log.w("AA", "imagePath = " + imagePath);
            }
        } finally {

        }

        // 缩略图ID:MediaStore.Audio.Media._ID
        try {
            int index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            if (index >= 0) {
                int imageId = cursor.getInt(index);
                Log.w("AA", "imageId = " + imageId);
            }
        } finally {

        }
    }

    File getFile() {
        File file = this.getExternalCacheDir();
        file = new File(file, "video_capture2");
        file.mkdirs();

        file = new File(file, "tmp.mp4.capture");
        return file;
    }

    int getDuration(Uri uri) {
        int duration = -1;

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();

            duration = mediaPlayer.getDuration();   // 获取到的是毫秒值
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        return duration;

    }

    Point getSize(File file) {
        if (!file.exists()) {
            return new Point(0,0);
        }

        int width = -1;
        int height = -1;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());

        {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            if (!TextUtils.isEmpty(value)) {
                width = Integer.parseInt(value);
            }
        }

        {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (!TextUtils.isEmpty(value)) {
                height = Integer.parseInt(value);
            }
        }

        if (width > 0 && height > 0) {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (!TextUtils.isEmpty(value)) {
                int rotate = Integer.parseInt(value);
                if (rotate == 90 || rotate == 270) {
                    int tmp = height;
                    height = width;
                    width = tmp;
                }
            }
        }

        retriever.release();

        width = (width < 0)? 0: width;
        height = (height < 0)? 0: height;
        return new Point(width, height);
    }

    CharSequence getInfo(File file) {
        if (!file.exists()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());

        {
            int value = getDuration(Uri.fromFile(file));
            sb.append("duration = " + value);
            sb.append('\n');
        }

        {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            sb.append("video duration = " + value);
            sb.append('\n');
        }

        {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            sb.append("video width = " + value);
            sb.append('\n');
        }

        {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            sb.append("video height = " + value);
            sb.append('\n');
        }


        {
            String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            sb.append("video rotation = " + value);
            sb.append('\n');
        }

        retriever.release();

        return sb;
    }
}
