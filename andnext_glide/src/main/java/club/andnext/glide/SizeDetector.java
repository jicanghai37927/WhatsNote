package club.andnext.glide;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.ExifInterfaceImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SizeDetector {

    private static String TAG = SizeDetector.class.getSimpleName();

    int width;
    int height;
    int orientation;
    int rotate;

    private final ArrayPool byteArrayPool;
    private final List<ImageHeaderParser> parsers;

    Context context;

    public SizeDetector(Context context) {
        this.context = context;

        {
            this.byteArrayPool = new LruArrayPool(20 * 1024);
        }

        {
            this.parsers = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                parsers.add(new ExifInterfaceImageHeaderParser());
            }
            parsers.add(new DefaultImageHeaderParser());
        }

    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean parse(Uri uri) {

        {
            this.reset();
        }

        {
            int[] size = getSize(context, uri);
            this.width = size[0];
            this.height = size[1];
        }

        {
            this.orientation = getOrientation(context, uri, parsers, byteArrayPool);
            this.rotate = TransformationUtils.getExifOrientationDegrees(orientation);
        }

        if (rotate == 90 || rotate == 270) {
            int value = this.width;
            this.width = this.height;
            this.height = value;
        }


        Log.v(TAG, uri + ": width = " + width + ", height = " + height);

        return (this.width > 0 && this.height > 0);
    }

    static int[] getSize(Context context, Uri uri) {
        InputStream is = null;

        int w = 0;
        int h = 0;

        try {

            is = context.getContentResolver().openInputStream(uri);

            {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeStream(is, null, options); // 此时返回的bitmap为null
                w = options.outWidth;
                h = options.outHeight;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new int[] {w, h};
    }

    static int getOrientation(Context context, Uri uri, @NonNull List<ImageHeaderParser> parsers, @NonNull ArrayPool byteArrayPool) {

        int orientation = ImageHeaderParser.UNKNOWN_ORIENTATION;
        InputStream is = null;

        try {

            is = context.getContentResolver().openInputStream(uri);

            orientation = ImageHeaderParserUtils.getOrientation(parsers, is, byteArrayPool);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return orientation;
    }

    void reset() {
        this.width = 0;
        this.height = 0;
        this.orientation = ExifInterface.ORIENTATION_NORMAL;
        this.rotate = 0;
    }

    public static Point getVideoSize(File file) {
        if (!file.exists()) {
            return new Point(0,0);
        }

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());

        Point size = getVideoSize(retriever);

        retriever.release();

        return size;
    }

    public static Point getVideoSize(MediaMetadataRetriever retriever) {

        if (retriever == null) {
            return new Point(0, 0);
        }

        int width = -1;
        int height = -1;

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

        width = (width < 0)? 0: width;
        height = (height < 0)? 0: height;
        return new Point(width, height);
    }
}
