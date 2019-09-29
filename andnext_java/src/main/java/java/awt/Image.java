package java.awt;

import android.graphics.Bitmap;

public class Image {

    protected Bitmap bitmap;

    public Image(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getWidth(Object object) {
        return bitmap.getWidth();
    }

    public int getHeight(Object object) {
        return bitmap.getHeight();
    }

}
