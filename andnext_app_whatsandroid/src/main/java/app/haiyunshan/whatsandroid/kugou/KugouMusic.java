package app.haiyunshan.whatsandroid.kugou;

import com.google.gson.annotations.SerializedName;

public class KugouMusic {

    @SerializedName("hash")
    public String hash;

    @SerializedName("songName")
    public String songName;

    @SerializedName("singerName")
    public String singerName;

    @SerializedName("choricSinger")
    public String choricSinger;

    @SerializedName("album_name")
    public String album_name; // always be null

    @SerializedName("album_img")
    public String album_img;

    @Override
    public String toString() {
        return "KugouMusic{" +
                "hash='" + hash + '\'' +
                ", songName='" + songName + '\'' +
                ", singerName='" + singerName + '\'' +
                ", choricSinger='" + choricSinger + '\'' +
                ", album_name='" + album_name + '\'' +
                ", album_img='" + album_img + '\'' +
                '}';
    }
}
