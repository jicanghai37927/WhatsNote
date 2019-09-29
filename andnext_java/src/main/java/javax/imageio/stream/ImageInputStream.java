package javax.imageio.stream;

import java.io.ByteArrayInputStream;

public class ImageInputStream {

    ByteArrayInputStream stream;

    public ImageInputStream(ByteArrayInputStream stream) {
        this.stream = stream;
    }

    public ByteArrayInputStream getStream() {
        return stream;
    }
}
