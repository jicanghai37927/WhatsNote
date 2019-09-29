package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class ImageIO {

    public static final String MIME_TYPE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_PNG  = "image/png";
    public static final String MIME_TYPE_GIF  = "image/gif";
    public static final String MIME_TYPE_BMP  = "image/bmp";
    public static final String MIME_TYPE_TIFF = "image/tiff";
    public static final String MIME_TYPE_PDF  = "image/pdf";
    public static final String MIME_TYPE_PICT = "image/x-pict";

    /**
     * Sometimes this is used for jpg instead :or have I made this up
     */
    public static final String MIME_TYPE_JPG  = "image/jpg";

    public static ImageInputStream createImageInputStream(ByteArrayInputStream byteArrayInputStream) {
        return new ImageInputStream(byteArrayInputStream);
    }

    public static ImageOutputStream createImageOutputStream(ByteArrayOutputStream outputStream) {
        return new ImageOutputStream(outputStream);
    }

    public static BufferedImage read(ByteArrayInputStream byteArrayInputStream) {
        return read(new ImageInputStream(byteArrayInputStream));
    }

    public static BufferedImage read(ImageInputStream imageInputStream) {
        Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream.getStream());

        return new BufferedImage(bitmap);
    }

    public static void write(BufferedImage image, String mimeType, ByteArrayOutputStream outputStream) {
        Bitmap.CompressFormat format = null;
        if (mimeType.equalsIgnoreCase(MIME_TYPE_JPEG)
            || mimeType.equalsIgnoreCase(MIME_TYPE_JPG)) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (mimeType.equalsIgnoreCase(MIME_TYPE_PNG)) {
            format = Bitmap.CompressFormat.PNG;
        }

        if (format == null) {
            return;
        }

        image.getBitmap().compress(format, 60, outputStream);

    }

    public static String[] getWriterMIMETypes() {
        return new String[] {
                MIME_TYPE_JPEG,
                MIME_TYPE_JPG,
                MIME_TYPE_PNG
        };
    }

    public static String[] getReaderMIMETypes() {
        return new String[] {
                MIME_TYPE_JPEG,
                MIME_TYPE_JPG,
                MIME_TYPE_PNG
        };
    }

    public static Iterator<ImageWriter> getImageWritersByMIMEType(String mimeType) {

        ArrayList<ImageWriter> list = new ArrayList<>();

        Bitmap.CompressFormat format = null;
        if (mimeType.equalsIgnoreCase(MIME_TYPE_JPEG)
                || mimeType.equalsIgnoreCase(MIME_TYPE_JPG)) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (mimeType.equalsIgnoreCase(MIME_TYPE_PNG)) {
            format = Bitmap.CompressFormat.PNG;
        }

        if (format != null) {
            list.add(new ImageWriter(format));
        }

        return list.iterator();
    }
}
