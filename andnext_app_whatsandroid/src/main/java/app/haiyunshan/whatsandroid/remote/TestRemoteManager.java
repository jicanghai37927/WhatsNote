package app.haiyunshan.whatsandroid.remote;

import android.content.Context;
import app.haiyunshan.whatsandroid.WhatsApp;
import club.andnext.download.DownloadAgent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class TestRemoteManager {

    private static final long TIME_OUT = 5;

    private static TestRemoteManager instance;

    String baseUrl;
    TestRemoteService service;

    DownloadAgent downloadAgent;

    OkHttpClient httpClient;

    Context context;

    public static TestRemoteManager getInstance() {
        if (instance == null) {
            instance = new TestRemoteManager();
        }

        return instance;
    }

    private TestRemoteManager() {
        this.context = WhatsApp.getInstance();

        this.baseUrl = "http://andnext.club/whatsnote/";

        OkHttpClient okHttpClient = this.createHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.downloadAgent = DownloadAgent.create(context, okHttpClient, new File(context.getExternalFilesDir(null), "download_ds.json"));

        this.service = retrofit.create(TestRemoteService.class);
        this.httpClient = okHttpClient;
    }

    public TestRemoteService getService() {
        return service;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public DownloadAgent getDownloadAgent() {
        return downloadAgent;
    }

    OkHttpClient createHttpClient() {

        OkTrustManager trustManager = new OkTrustManager();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory(trustManager), trustManager)
                .hostnameVerifier((hostname, session) -> true)
                .addNetworkInterceptor(new DownloadInterceptor())
                .build();

        return okHttpClient;
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
