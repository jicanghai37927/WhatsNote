package app.haiyunshan.whatsnote.remote;

import android.content.Context;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.config.ConfigEntity;
import club.andnext.download.DownloadAgent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class RemoteManager {

    private static final long TIME_OUT = 30;

    private static RemoteManager instance;

    String baseUrl;

    SongService songService;

    RemoteService service;
    DownloadAgent downloadAgent;
    OkHttpClient httpClient;

    Context context;

    public static RemoteManager getInstance() {
        if (instance == null) {
            instance = new RemoteManager();
        }

        return instance;
    }

    private RemoteManager() {
        this.context = WhatsApp.getInstance();

        this.baseUrl = ConfigEntity.getBaseUrl();
    }

    public RemoteService getService() {
        if (this.service != null) {
            return this.service;
        }

        OkHttpClient okHttpClient = this.getHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.service = retrofit.create(RemoteService.class);
        return service;
    }

    public SongService getSongService() {
        if (this.songService != null) {
            return this.songService;
        }

        String rootUrl = "https://music.163.com/m/";

        OkHttpClient okHttpClient = this.getHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(rootUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.songService = retrofit.create(SongService.class);
        return songService;
    }


    public DownloadAgent getDownloadAgent() {
        if (this.downloadAgent != null) {
            return this.downloadAgent;
        }

        OkHttpClient okHttpClient = getHttpClient();
        File file = context.getExternalFilesDir("download");
        file = new File(file, "download_ds.json");

        this.downloadAgent = DownloadAgent.create(context, okHttpClient, file);
        return downloadAgent;
    }

    public OkHttpClient getHttpClient() {

        if (this.httpClient != null) {
            return this.httpClient;
        }

        OkTrustManager trustManager = new OkTrustManager();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
                .hostnameVerifier((hostname, session) -> true)
                .addNetworkInterceptor(new DownloadInterceptor())
                .build();

        this.httpClient = okHttpClient;
        return httpClient;
    }

    private static SSLSocketFactory createSSLSocketFactory(X509TrustManager trustManager) {

        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;

    }

    /**
     *
     */
    private class DownloadInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = null;
            if (downloadAgent != null) {
                response = downloadAgent.intercept(chain);
            }

            if (response == null) {
                response = chain.proceed(chain.request());
            }

            return response;
        }
    }

    /**
     *
     */
    private static class OkTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

}
