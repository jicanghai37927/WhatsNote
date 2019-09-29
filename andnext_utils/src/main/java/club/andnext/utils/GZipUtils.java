package club.andnext.utils;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

    public static byte[] encode(@NonNull byte[] t) {

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(t);
            gzip.close();

            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();

            return encode;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decode(@NonNull byte[] t) {

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);

            byte[] buffer = new byte[10 * 1024];
            int n;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }

            gzip.close();
            in.close();
            out.close();

            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
