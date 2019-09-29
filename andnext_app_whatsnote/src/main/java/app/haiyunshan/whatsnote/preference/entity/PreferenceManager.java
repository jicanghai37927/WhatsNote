package app.haiyunshan.whatsnote.preference.entity;

import android.content.Context;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.preference.dataset.PreferenceDataset;
import club.andnext.utils.GsonUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

class PreferenceManager {

    File file;
    PreferenceDataset dataset;
    PreferenceEntity entity;

    static PreferenceManager instance;

    static final PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }

        return instance;
    }

    private PreferenceManager(Context context) {

        {
            File dir = Directory.get(context, Directory.DIR_PREFERENCE);
            this.file = new File(dir, "pref_ds.json");
        }

        {
            this.dataset = GsonUtils.fromJson(file, PreferenceDataset.class);
            if (dataset == null) {
                this.dataset = new PreferenceDataset();
            }
        }

    }

    PreferenceDataset getDataset() {
        return dataset;
    }

    long save() {
        GsonUtils.toJson(dataset, file);
        return 1;
    }
}
