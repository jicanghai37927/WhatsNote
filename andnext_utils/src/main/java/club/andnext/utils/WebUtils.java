package club.andnext.utils;

public class WebUtils {

    public static final String withTimestamp(String url) {

        String args = "ts=" + System.currentTimeMillis();
        url = url + "?" + args;

        return url;
    }
}
