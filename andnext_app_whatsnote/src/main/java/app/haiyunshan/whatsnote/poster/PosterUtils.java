package app.haiyunshan.whatsnote.poster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import club.andnext.utils.AssetUtils;

public class PosterUtils {

    public static Uri getPosterUri(Context context) {
        File file = getPosterFile(context);
        if (!file.exists()) {
            return null;
        }

        Uri uri = getImageContentUri(context, file);
        return uri;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }

    static File getPosterFile(Context context) {
        File dir = getDir(context);

        String name = "poster.jpg";
        String source = "poster/" + name;
        File file = new File(dir, name);
        if (!file.exists()) {
            AssetUtils.copyToFile(context, source, file);
        }

        return file;
    }

    static File getDir(Context context) {
        File file = context.getExternalFilesDir("poster");
        file.mkdirs();

        return file;
    }
}
