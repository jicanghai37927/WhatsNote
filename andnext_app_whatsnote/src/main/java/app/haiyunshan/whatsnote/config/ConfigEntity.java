package app.haiyunshan.whatsnote.config;

import android.content.Context;
import android.text.TextUtils;
import app.haiyunshan.whatsnote.WhatsApp;
import club.andnext.utils.GsonUtils;

import java.io.File;

public class ConfigEntity {

    private static ConfigEntity instance;

    ConfigEntry entry;
    ConfigEntry defaultEntry;

    static ConfigEntity getInstance() {
        if (instance == null) {
            instance = new ConfigEntity();
        }

        return instance;
    }

    private ConfigEntity() {
        File file = getExternalFile(WhatsApp.getInstance());
        if (file.exists()) {
            this.entry = GsonUtils.fromJson(file, ConfigEntry.class);
        }

        if (entry == null) {
            entry = new ConfigEntry();
            defaultEntry = entry;
        }

        if (defaultEntry == null) {
            defaultEntry = new ConfigEntry();
        }
    }

    public static final boolean isDebug() {
        return getInstance().isDebugPrivate();
    }

    public static final String getBaseUrl() {
        return getInstance().getBaseUrlPrivate();
    }

    boolean isDebugPrivate() {
        return entry.isDebug();
    }

    String getBaseUrlPrivate() {
        String value = entry.getBaseUrl();
        if (TextUtils.isEmpty(value)) {
            value = defaultEntry.getBaseUrl();
        }

        return value;
    }

    File getExternalFile(Context context) {
        File file = context.getExternalFilesDir("config");
        file = new File(file, "config_ds.json");
        return file;
    }
}
