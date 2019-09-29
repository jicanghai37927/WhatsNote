package club.andnext.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    public static final void writeBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat format, int quality, File file) {
        write(bitmap, format, quality, file);
    }

    private static final void write(Bitmap bitmap, Bitmap.CompressFormat format, int quality, File file) {

        FileOutputStream fos = null;

        try {

            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(format, quality, fos);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private static final void compress(Bitmap bitmap, Bitmap.CompressFormat format, int quality, File file) {

        FileOutputStream fos = null;

        try {

            file.createNewFile();
            fos = new FileOutputStream(file);

            fos.write(compress(bitmap, format, quality));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private static final byte[] compress(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {

        ByteArrayOutputStream fos = new ByteArrayOutputStream(200 * 1024);
        bitmap.compress(format, quality, fos);

        byte[] data = fos.toByteArray();

        try {
            fos.close();
        } catch (IOException e) {
        }

        return data;
    }

}
