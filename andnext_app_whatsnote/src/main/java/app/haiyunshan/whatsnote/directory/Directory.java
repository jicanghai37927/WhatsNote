package app.haiyunshan.whatsnote.directory;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.File;

public class Directory {

    public static final String DIR_NOTE         = "WhatsNote/note";

    public static final String DIR_SONG         = "WhatsNote/song";

    public static final String DIR_PREFERENCE   = "WhatsNote/preference";

    public static final String DIR_FOOTER       = "WhatsNote/preference/footer";

    public static File get(Context context, @NonNull String path) {

        File dir = context.getExternalFilesDir(null);
        dir = new File(dir, path);
        dir.mkdirs();

        return dir;
    }
}
