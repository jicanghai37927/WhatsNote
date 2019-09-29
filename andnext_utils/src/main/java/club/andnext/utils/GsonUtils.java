package club.andnext.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.*;
import java.lang.reflect.Type;

public class GsonUtils {

    static final String TAG = "GsonUtils";

    public static final String toJson(Object obj) {

        long a = System.currentTimeMillis();

        String text;
        {
            Gson gson = new Gson();

            Appendable writer = new StringWriter(200 * 1024);
            gson.toJson(obj, writer);

            text = writer.toString();
        }

        Log.v(TAG, obj.getClass().getSimpleName() + " = " + (System.currentTimeMillis() - a));

        return text;
    }

    public static final File toJson(Object obj, File file) {
        file.getAbsoluteFile().getParentFile().mkdirs();

        long a = System.currentTimeMillis();

        try {

            String text = toJson(obj);
            FileUtils.writeStringToFile(file, text, "utf-8", false);

        } catch (Exception e) {
            file = null;
        }

        Log.v(TAG, "[File]" + obj.getClass().getSimpleName() + " = " + (System.currentTimeMillis() - a));

        return file;
    }

    public static final <T> T fromJson(String text, Class<T> classOfT) {

        if (TextUtils.isEmpty(text)) {
            return null;
        }

        Gson gson = new Gson();
        T ds = gson.fromJson(text, classOfT);

        return ds;
    }

    public static final <T> T fromJson(Context context, String path, Class<T> classOfT) {

        try {
            InputStream is = context.getAssets().open(path);
            InputStreamReader isr = new InputStreamReader(is, "utf-8");

            Gson gson = new Gson();
            T ds = gson.fromJson(isr, classOfT);

            isr.close();
            is.close();

            return ds;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final <T> T fromJson(File file, Class<T> classOfT) {
        return fromJson(file, classOfT, null);
    }

    public static final <T> T fromJson(File file, Class<T> classOfT, Pair<Type, JsonDeserializer>... typeAdapter) {

        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");

            Gson gson;
            if (typeAdapter != null) {
                GsonBuilder builder = new GsonBuilder();
                for (Pair<Type, JsonDeserializer> p : typeAdapter) {
                    builder.registerTypeAdapter(p.first, p.second);
                }
                gson = builder.create();
            } else {
                gson = new Gson();
            }

            T ds = gson.fromJson(isr, classOfT);

            isr.close();
            fis.close();

            return ds;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
