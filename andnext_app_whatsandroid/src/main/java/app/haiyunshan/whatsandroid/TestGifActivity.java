package app.haiyunshan.whatsandroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.utils.FileUtils;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TestGifActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gif);
    }

    void onTestClick(View view) {

        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "com.izaodao.gps");
        file = new File(file, "h0_a.gif");

        File inFile = file;
        try {
            Bitmap bitmap = getLastBitmap(file);

            if (bitmap == null) {
                Log.w("AA", "失败");
            } else {
                Log.w("AA", "width = " + bitmap.getWidth());


                file = new File(file, inFile.getName());
                file.createNewFile();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void onTransformClick(View view) {
        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "com.izaodao.gps");

        File[] array = file.listFiles(pathname -> {
            if (pathname.isDirectory()) {
                return false;
            }

            return pathname.getName().endsWith(".gif");
        });

        File dir = Environment.getExternalStorageDirectory();
        dir = new File(dir, "com.izaodao.gps_lastframe");
        dir.mkdirs();

        for (File f : array) {
            File in = f;
            File out = new File(dir, in.getName() + ".png");

            transform(in, out);
        }
    }

    void transform(File in, File out) {
        File inFile = in;
        try {
            Bitmap bitmap = getLastBitmap(inFile);

            if (bitmap == null) {
                Log.w("AA", "失败");
            } else {
                Log.w("AA", "width = " + bitmap.getWidth());

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(out));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Bitmap getLastBitmap(File file) throws IOException {
        Bitmap bitmap = null;

        byte[] data = FileUtils.readFileToByteArray(file);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        GifHeaderParser parser = new GifHeaderParser().setData(data);
        GifHeader header = parser.parseHeader();

        GifDecoder decoder = new StandardGifDecoder(provider, header, buffer, 1);

        int width = decoder.getWidth();
        int height = decoder.getHeight();

        Bitmap out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);

        int count = decoder.getFrameCount();
        while (true) {
            decoder.advance();
            bitmap = decoder.getNextFrame();

            canvas.drawBitmap(bitmap, 0, 0, null);

            int index = decoder.getCurrentFrameIndex();
            if (index + 1 == count) {
                break;
            }
        }

        Log.w("AA", "count = " + count);

        return out;
    }

    private com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider provider = new GifDecoder.BitmapProvider() {

        @NonNull
        @Override
        public Bitmap obtain(int width, int height, @NonNull Bitmap.Config config) {
            return Bitmap.createBitmap(width, height, config);
        }

        @Override
        public void release(@NonNull Bitmap bitmap) {
            bitmap.recycle();
        }

        @NonNull
        @Override
        public byte[] obtainByteArray(int size) {
            return new byte[size];
        }

        @Override
        public void release(@NonNull byte[] bytes) {

        }

        @NonNull
        @Override
        public int[] obtainIntArray(int size) {
            return new int[size];
        }

        @Override
        public void release(@NonNull int[] array) {

        }
    };
}
