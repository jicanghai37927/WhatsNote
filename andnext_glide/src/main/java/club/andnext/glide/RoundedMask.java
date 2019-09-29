package club.andnext.glide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class RoundedMask extends BitmapTransformation {

    private static final String ID = RoundedMask.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    final GradientDrawable drawable;

    public RoundedMask(GradientDrawable d) {
        this.drawable = d;
    }

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        Bitmap bitmap = TransformationUtils.roundedCorners(pool, toTransform, (int)drawable.getCornerRadius());

        Canvas canvas = new Canvas(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        canvas.setBitmap(null);
        return bitmap;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), (int)(drawable.getCornerRadius()));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);

        {
            byte[] radiusData = ByteBuffer.allocate(4).putFloat(drawable.getCornerRadius()).array();
            messageDigest.update(radiusData);
        }

    }
}