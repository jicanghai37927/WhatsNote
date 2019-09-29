package club.andnext.utils;

import android.graphics.Color;
import androidx.annotation.ColorInt;

public class ColorUtils {

    public static final String format(int color) {
        if (color == Color.TRANSPARENT) {
            return "#00000000";
        }

        String str = Integer.toHexString(color);
        if (str.length() < 6) { // 补足6位
            int count = (6 - str.length());
            for (int i = 0; i < count; i++) {
                str = "0" + str;
            }
        } else if (str.length() == 7) { // 补足8位
            str = "0" + str;
        }

        return "#" + str;
    }

    public static final int parse(String colorString) {
        return Color.parseColor(colorString);
    }

    public static boolean isColorDark(@ColorInt int color){
        double value = calculateLuminance(color);
        return (value < 0.5f);
    }

    public static final double calculateLuminance(@ColorInt int color) {
        double value = 0;

        value += 0.299 * Color.red(color);
        value += 0.587 * Color.green(color);
        value += 0.114 * Color.blue(color);

        value = 1 - value / 255;

        return value;
    }
}
