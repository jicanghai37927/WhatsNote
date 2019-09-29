package app.haiyunshan.whatsnote.preference.dataset;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PreferenceDataset implements Serializable {

    @SerializedName("profile")
    ProfileEntry profile;

    @SerializedName("auto_download")
    boolean autoDownload = true;

    @SerializedName("chat_extension")
    boolean chatExtension = true;

    @SerializedName("speaker")
    boolean speaker = true;

    public PreferenceDataset() {
        this.profile = new ProfileEntry();
    }

    public ProfileEntry getProfile() {
        return profile;
    }

    public boolean isAutoDownload() {
        return autoDownload;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    public boolean isChatExtension() {
        return chatExtension;
    }

    public void setChatExtension(boolean chatExtension) {
        this.chatExtension = chatExtension;
    }

    public boolean isSpeakerOn() {
        return this.speaker;
    }

    public void setSpeakerOn(boolean on) {
        this.speaker = on;
    }
}
