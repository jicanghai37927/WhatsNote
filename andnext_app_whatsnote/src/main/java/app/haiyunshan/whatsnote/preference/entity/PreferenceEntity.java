package app.haiyunshan.whatsnote.preference.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.preference.dataset.PreferenceDataset;

public class PreferenceEntity {

    ProfileEntity profileEntity;

    PreferenceDataset entry;

    private PreferenceEntity(@NonNull Context context, @NonNull PreferenceDataset ds) {
        this.entry = ds;

        this.profileEntity = new ProfileEntity(context, ds.getProfile());
    }

    public ProfileEntity getProfile() {
        return profileEntity;
    }

    public boolean isAutoDownload() {
        return entry.isAutoDownload();
    }

    public void setAutoDownload(boolean autoDownload) {
        entry.setAutoDownload(autoDownload);
    }

    public boolean isChatExtension() {
        return entry.isChatExtension();
    }

    public void setChatExtension(boolean chatExtension) {
        entry.setChatExtension(chatExtension);
    }

    public boolean isSpeakerOn() {
        return entry.isSpeakerOn();
    }

    public void setSpeakerOn(boolean on) {
        entry.setSpeakerOn(on);
    }

    public long save() {
        Context context = WhatsApp.getInstance();

        PreferenceManager mgr = PreferenceManager.getInstance(context);
        return mgr.save();
    }

    public static final PreferenceEntity obtain() {
        return Factory.obtain(WhatsApp.getInstance());
    }

    /**
     *
     */
    static class Factory {

        static final PreferenceEntity obtain(Context context) {
            PreferenceManager mgr = PreferenceManager.getInstance(context);
            if (mgr.entity == null) {
                mgr.entity = create(context);
            }

            return mgr.entity;
        }

        static final PreferenceEntity create(Context context) {
            PreferenceManager mgr = PreferenceManager.getInstance(context);
            mgr.entity = new PreferenceEntity(context, mgr.getDataset());

            return mgr.entity;
        }
    }
}
