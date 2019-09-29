package javax.imageio.stream;

import java.io.ByteArrayOutputStream;

public class ImageOutputStream {

    ByteArrayOutputStream stream;

    public ImageOutputStream(ByteArrayOutputStream stream) {
        this.stream = stream;
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }
}
