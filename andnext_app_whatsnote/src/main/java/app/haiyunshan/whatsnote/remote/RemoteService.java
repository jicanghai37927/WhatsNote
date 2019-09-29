package app.haiyunshan.whatsnote.remote;

import app.haiyunshan.whatsnote.update.VersionEntry;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RemoteService {

    @GET("update/version.json")
    Observable<VersionEntry> getVersion();

}
