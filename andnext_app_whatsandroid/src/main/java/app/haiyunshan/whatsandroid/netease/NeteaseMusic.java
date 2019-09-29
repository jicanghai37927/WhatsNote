package app.haiyunshan.whatsandroid.netease;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NeteaseMusic {

    @SerializedName("songs")
    public List<Song> songs;

    @Override
    public String toString() {
        return "NeteaseMusic{" +
                "songs=" + songs +
                '}';
    }

    /**
     *
     */
    public static final class Song {

        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("artists")
        public List<Artist> artists;

        @SerializedName("album")
        public Album album;

        @Override
        public String toString() {
            return "Song{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", artists=" + artists +
                    ", album=" + album +
                    '}';
        }
    }

    /**
     *
     */
    public static final class Artist {

        @SerializedName("name")
        public String name;

        @Override
        public String toString() {
            return "Artist{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    /**
     *
     */
    public static final class Album {

        @SerializedName("name")
        public String name;

        @SerializedName("blurPicUrl")
        public String blurPicUrl;

        @SerializedName("picUrl")
        public String picUrl;

        @Override
        public String toString() {
            return "Album{" +
                    "name='" + name + '\'' +
                    ", blurPicUrl='" + blurPicUrl + '\'' +
                    ", picUrl='" + picUrl + '\'' +
                    '}';
        }
    }
}
