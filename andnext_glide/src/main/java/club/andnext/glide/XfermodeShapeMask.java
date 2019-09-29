package club.andnext.glide;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class XfermodeShapeMask extends BitmapTransformation {

    private static final String ID = XfermodeShapeMask.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    final int resId;
    final Drawable drawable;

    public XfermodeShapeMask(Context context, int resId) {
        this.resId = resId;
        this.drawable = context.getDrawable(resId);
    }

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        Bitmap bitmap = pool.get(toTransform.getWidth(), toTransform.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(canvas);

        canvas.drawBitmap(toTransform, 0, 0, paint);

        canvas.setBitmap(null);
        return bitmap;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), resId);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);

        {
            byte[] radiusData = ByteBuffer.allocate(4).putInt(resId).array();
            messageDigest.update(radiusData);
        }
    }
}