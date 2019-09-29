package app.haiyunshan.whatsandroid.remote;

import app.haiyunshan.whatsandroid.kugou.KugouMusic;
import app.haiyunshan.whatsandroid.netease.NeteaseMusic;
import app.haiyunshan.whatsandroid.update.VersionEntry;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TestRemoteService {

    @GET("update/version.json")
    Observable<VersionEntry> getVersion();

    @FormUrlEncoded
    @POST("http://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo")
    Observable<KugouMusic> getKugouMusic(@Field("hash") String hash);

    @FormUrlEncoded
    @POST("http://music.163.com/api/song/detail")
    Observable<NeteaseMusic> getNeteaseMusic(@Field("ids") String hash);
}
