package app.haiyunshan.whatsnote.config;

import com.google.gson.annotations.SerializedName;

class ConfigEntry {

    @SerializedName("debug")
    boolean debug = false;

    @SerializedName("baseUrl")
    String baseUrl = "http://andnext.club/whatsnote/master/";

    ConfigEntry() {

    }

    public boolean isDebug() {
        return debug;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
