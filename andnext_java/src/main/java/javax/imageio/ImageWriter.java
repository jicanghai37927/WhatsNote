package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;

import javax.imageio.stream.ImageOutputStream;

public class ImageWriter {

    ImageOutputStream stream;

    Bitmap.CompressFormat format;

    public ImageWriter(Bitmap.CompressFormat format) {
        this.format = format;
    }

    public void setOutput(ImageOutputStream stream) {
        this.stream = stream;
    }

    public void write(BufferedImage bi) {
        bi.getBitmap().compress(format, 60, stream.getStream());
    }
}
