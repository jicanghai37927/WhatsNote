package app.haiyunshan.whatsnote.remote;

import io.reactivex.Observable;
import retrofit2.http.*;

public interface SongService {

    String USER_AGENT = "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1";

    @Headers(USER_AGENT)
    @GET("https://music.163.com/m/song")
    Observable<String> getSongFromWeb(@Query("id") String id);

    @Headers(USER_AGENT)
    @GET("https://api.imjad.cn/cloudmusic/?type=detail")
    Observable<String> getSongFromImjad(@Query("id") String id);

    @Headers(USER_AGENT)
    @GET("https://api.imjad.cn/cloudmusic/?type=lyric")
    Observable<String> getLyricFromImjad(@Query("id") String id);

    @Headers(USER_AGENT)
    @GET("http://music.163.com/api/song/detail")
    Observable<String> getSongFromNetease(@Query("id") String id, @Query("ids") String ids);

    @Headers(USER_AGENT)
    @GET("http://music.163.com/api/song/lyric?os=iphone&lv=-1&kv=-1&tv=-1")
    Observable<String> getLyricFromNetease(@Query("id") String id);

    @Headers(USER_AGENT)
    @GET("https://v1.itooi.cn/netease/song")
    Observable<String> getSongFromMessApi(@Query("id") String id);

    @Headers(USER_AGENT)
    @GET("http://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo")
    Observable<String> getSongFromKugou(@Query("hash") String hash);

    @Headers(USER_AGENT)
    @GET("http://mobilecdnbj.kugou.com/api/v3/search/song?format=json&page=1&pagesize=20&showtype=1")
    Observable<String> searchSongFromKugou(@Query("keyword") String keyword);

    @Headers(USER_AGENT)
    @GET("http://mobilecdnbj.kugou.com/api/v3/album/info")
    Observable<String> getAlbumFromKugou(@Query("albumid") String albumid);

    @Headers(USER_AGENT)
    @GET("http://lyrics.kugou.com/search?ver=1&man=yes&client=mobi&keyword=&duration=&album_audio_id=")
    Observable<String> searchLyricFromKugou(@Query("hash") String hash);

    @Headers(USER_AGENT)
    @GET("http://lyrics.kugou.com/download?ver=1&client=iphone&fmt=&charset=utf8")
    Observable<String> getLyricFromKugou(@Query("id") String id, @Query("accesskey") String accesskey);

}
