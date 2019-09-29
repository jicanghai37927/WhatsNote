package app.haiyunshan.whatsnote;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import app.haiyunshan.whatsnote.config.ConfigEntity;
import app.haiyunshan.whatsnote.helper.CrashHelper;
import club.andnext.xsltml.MathMLTransformer;

public class WhatsApp extends Application {

    private static WhatsApp instance;

    MathMLTransformer mathMLTransformer;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // day mode only now.
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (ConfigEntity.isDebug()) {
            CrashHelper.attach(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public MathMLTransformer getMathMLTransformer() {
        if (mathMLTransformer == null) {
            mathMLTransformer = new MathMLTransformer(this);
        }

        return mathMLTransformer;
    }

    public static final WhatsApp getInstance() {
        return instance;
    }
}
