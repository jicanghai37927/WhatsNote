package app.haiyunshan.whatsdownloader;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class DownloadManager {

    private static final long TIME_OUT = 5;

    private static DownloadManager instance;

    OkHttpClient httpClient;

    public static DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }

        return instance;
    }

    private DownloadManager() {

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
            Response response = chain.proceed(chain.request());

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
