package club.andnext.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public class Utils {

    public static String getTitle(CharSequence s, int max) {
        if (TextUtils.isEmpty(s)) {
            return s.toString();
        }

        String text;
        if (s.length() > max) {
            text = s.subSequence(0, max).toString().trim();
        } else {
            text = s.toString().trim();
        }

        int pos = text.indexOf('\n');
        if (pos > 0) {
            text = text.substring(0, pos);
        }

        text = text.trim();
        return text;
    }

    public static final int getDisplayWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }
}
