package app.haiyunshan.whatsnote.preference.dataset;

import com.google.gson.annotations.SerializedName;

public class ProfileEntry {

    @SerializedName("name")
    String name;

    @SerializedName("signature")
    String signature;

    @SerializedName("portrait")
    String portrait;

    @SerializedName("vision")
    String vision;

    public ProfileEntry() {
        this.name = "";
        this.signature = "";
        this.portrait = "";
        this.vision = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }
}
