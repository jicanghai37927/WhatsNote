package app.haiyunshan.whatsnote.helper;

import android.app.Application;
import android.content.Context;
import app.haiyunshan.whatsnote.directory.Directory;

import java.io.*;

public class CrashHelper implements Thread.UncaughtExceptionHandler {

    Thread.UncaughtExceptionHandler defaultHandler;
    Context context;

    private static CrashHelper instance;

    public static final CrashHelper attach(Application context) {
        if (instance == null) {
            instance = new CrashHelper(context);
        }

        return instance;
    }

    private CrashHelper(Application context) {
        this.context = context;

        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        this.handleException(t, e);

        defaultHandler.uncaughtException(t, e);
    }

    boolean handleException(Thread t, Throwable e) {
        File dir = Directory.get(context, "crash");
        dir.mkdirs();

        File file = new File(dir, "crash_latest.txt");
        try {
            file.createNewFile();
            PrintWriter pw = new PrintWriter(file, "utf-8");
            e.printStackTrace(pw);
            pw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return true;
    }
}
