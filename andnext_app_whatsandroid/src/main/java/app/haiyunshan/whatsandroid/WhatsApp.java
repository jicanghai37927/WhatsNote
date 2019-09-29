package app.haiyunshan.whatsandroid;

import android.app.Application;

public class WhatsApp extends Application {

    private static WhatsApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

//        CrashHelper.attach(this); // Crash Helper, test only
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    public static final WhatsApp getInstance() {
        return instance;
    }
}
