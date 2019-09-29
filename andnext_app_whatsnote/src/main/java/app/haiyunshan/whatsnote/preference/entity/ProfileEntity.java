package app.haiyunshan.whatsnote.preference.entity;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.preference.dataset.ProfileEntry;
import club.andnext.utils.UUIDUtils;

import java.io.File;

public class ProfileEntity {

    public static final int VISION_WIDTH    = 2688;
    public static final int VISION_HEIGHT   = 1242;

    static final String keyPortrait = "portrait";
    static final String keyVision = "vision";

    ProfileEntry entry;

    Context context;

    ProfileEntity(@NonNull Context context, @NonNull ProfileEntry entry) {
        this.entry = entry;

        this.context = context.getApplicationContext();
    }

    public String getName() {
        return entry.getName();
    }

    public void setName(String name) {
        entry.setName(name);
    }

    public String getSignature() {
        return entry.getSignature();
    }

    public void setSignature(String signature) {
        entry.setSignature(signature);
    }

    public Uri getPortraitUri() {
        String name = entry.getPortrait();
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        String key = keyPortrait;
        String postfix = keyPortrait;

        Uri uri = getUri(key, name, postfix);
        return uri;
    }

    public Uri nextPortraitUri() {
        String name = UUIDUtils.next();
        String key = keyPortrait;
        String postfix = keyPortrait;

        Uri uri = getUri(key, name, postfix);
        return uri;
    }

    public void setPortraitUri(Uri uri) {
        String name = getName(uri);
        if (TextUtils.isEmpty(name)) {
            return;
        }

        entry.setPortrait(name);
    }

    public Uri getVisionUri() {
        String name = entry.getVision();
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        String key = keyVision;
        String postfix = keyVision;

        Uri uri = getUri(key, name, postfix);
        return uri;
    }

    public Uri nextVisionUri() {
        String name = UUIDUtils.next();
        String key = keyVision;
        String postfix = keyVision;

        Uri uri = getUri(key, name, postfix);
        return uri;
    }

    public void setVisionUri(Uri uri) {
        String name = getName(uri);
        if (TextUtils.isEmpty(name)) {
            return;
        }

        entry.setVision(name);
    }


    Uri getUri(String key, String name, String postfix) {
        File file = getDir(key);
        file = new File(file, name + "." + postfix);

        return Uri.fromFile(file);
    }

    File getDir(String key) {
        File dir = Directory.get(context, Directory.DIR_PREFERENCE);
        dir = new File(dir, key);
        dir.mkdirs();

        return dir;
    }

    String getName(Uri uri) {
        String name = uri.getLastPathSegment();
        int pos = name.lastIndexOf('.');
        if (pos > 0) {
            name = name.substring(0, pos);
        }

        return name;
    }

}
