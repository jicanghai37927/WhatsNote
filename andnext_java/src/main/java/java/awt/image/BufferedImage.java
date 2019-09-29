package java.awt.image;

import android.graphics.Bitmap;

import java.awt.Image;

public class BufferedImage extends Image {

    public BufferedImage(Bitmap bitmap) {
        super(bitmap);
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public static BufferedImage create(Image src, int size) {
        Bitmap bitmap = Bitmap.createScaledBitmap(src.getBitmap(), size, size, false);
        return new BufferedImage(bitmap);
    }
}
