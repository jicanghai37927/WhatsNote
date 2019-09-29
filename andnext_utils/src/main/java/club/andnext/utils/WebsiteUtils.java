/**
 * [Java提取域名或URL中的根域名](https://blog.csdn.net/kuluzs/article/details/51981212)
 * [URL判断(java)](https://www.cnblogs.com/xmyy/articles/2871871.html)
 * [Java获取URL中的顶级域名domain的工具类](https://www.cnblogs.com/DreamDrive/p/7594305.html)
 * [如何获取url中的顶级域名？](https://bbs.csdn.net/topics/300136318)
 *
 */
package club.andnext.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Patterns;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class WebsiteUtils {

    public static final String format(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        String lowercase = url.toLowerCase();
        if ((lowercase.startsWith("http://") || lowercase.startsWith("https://"))) {
            return url;
        }

        if (lowercase.startsWith("//")) {
            url = "http:" + url;
            return url;
        }

        url = "http://" + url;
        return url;
    }

    public static final int countOf(String text) {
        String regex = "[http|https]://";

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(text.toLowerCase());

        int count = 0;
        while (matcher.find()) {
            ++count;
        }

        return count;
    }

    public static final List<String> toSegments(String text) {

        Spannable s = addLinks(text);
        List<URLSpan> array = getURLSpans(s);

        int index = 0;
        ArrayList<String> matchList = new ArrayList<>();

        for (URLSpan object : array) {
            int start = s.getSpanStart(object);
            int end = s.getSpanEnd(object);

            if (start > index) {
                String match = text.substring(index, start);
                matchList.add(match);
            }

            if (end > start) {
                String match = text.substring(start, end);
                matchList.add(match);
            }

            index = end;
        }

        // Add remaining segment
        if (index < text.length()) {
            matchList.add(text.substring(index));
        }

        return matchList;
    }

    public static final List<URLSpan> getURLSpans(Spannable text) {
        List<URLSpan> list = new ArrayList<>();
        URLSpan[] array = text.getSpans(0, text.length(), URLSpan.class);
        if (array == null || array.length == 0) {
            return list;
        }

        for (URLSpan s : array) {
            String url = s.getURL().toLowerCase();
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                continue;
            }
            if (!isURL(url)) {
                continue;
            }

            int start = text.getSpanStart(s);
            int end = text.getSpanEnd(s);

            url = text.subSequence(start, end).toString().toLowerCase();
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                continue;
            }
            if (!isURL(url)) {
                continue;
            }

            char c;
            int pos = start;

            if (pos == 0) {
                list.add(s);
            } else {
                c = text.charAt(pos - 1);
                if (!((c >= 'a' && c <= 'z')
                        || (c >= 'A' && c <= 'Z')
                        || (c >= '0' && c <= '9'))) {

                    if (c == '(' && text.charAt(end - 1) == ')') {
                        text.removeSpan(s);

                        url = s.getURL();
                        s = new URLSpan(url.substring(0, url.length() - 1));
                        text.setSpan(s, start - 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    list.add(s);
                }
            }
        }

        Collections.sort(list, (a, b) -> {
            int m = text.getSpanStart(a);
            int n = text.getSpanStart(b);

            return (m - n);
        });

        return list;
    }

    public static final Spannable addLinks(String text) {
        String lowercase = text.toLowerCase();

        List<String> array = split(text, lowercase, "https://");
        List<String> list = new ArrayList<>();
        for (String str : array) {
            list.addAll(split(str, lowercase, "http://"));
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (String str : list) {
            Spannable s = new SpannableString(str);
            Linkify.addLinks(s, Linkify.WEB_URLS);
            ssb.append(s);
        }

        return ssb;
    }

    static List<String> split(String text, String lowercase, String key) {
        ArrayList<String> list = new ArrayList<>();

        int begin = 0;
        int pos = 0;
        while (true) {
            pos = lowercase.indexOf(key, pos);
            if (pos < 0) {
                break;
            }

            if (pos != 0) {
                list.add(text.substring(begin, pos));
            }

            begin = pos;
            pos += key.length();
        }

        if (begin == 0) {
            list.add(text);
            return list;
        }

        if (begin < text.length()) {
            list.add(text.substring(begin));
        }

        return list;
    }

    public static String decode(String url) {

        try {
            url = URLDecoder.decode(url,"UTF-8"); // decode
            url = URLDecoder.decode(url,"UTF-8"); // decode again
            url = URLDecoder.decode(url,"UTF-8"); // decode again and again, maybe enough
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static boolean isURL(String str) {
        boolean value = Patterns.WEB_URL.matcher(str).matches();
        if (!value) {
            return false;
        }

        for (int i = 0, length = str.length(); i < length; i++) {
            char c = str.charAt(i);
            if (c >= 0x4E00 && c <= 0x9FA5) { // chinese
                return false;
            }
        }

        return true;
    }

    public static String getHost(String url) {

        url = format(url);

        String host = "";

        try {

            host = new URL(url).getHost();

        } catch (Exception e) {

        }

        return host;
    }

    public static String getDomainName(String url) {

        url = format(url);

        String domain = "";

        try {
            String host = new URL(url).getHost();
            domain = host;

            Pattern p = Pattern.compile("[\\w-]+\\.(" + DOMAIN_RULES + ")\\b()*");
            Matcher matcher = p.matcher(host);
            if (matcher.find()) {
                domain = matcher.group();
            }

        } catch (Exception e) {

        }

        return domain;
    }

    static final String DOMAIN_RULES =
                            "com.cn" +
                            "|net.cn" +
                            "|org.cn" +
                            "|com.ag" +
                            "|net.ag" +
                            "|org.ag" +
                            "|com.br" +
                            "|net.br" +
                            "|com.bz" +
                            "|net.bz" +

                            "|com.co" +
                            "|net.co" +
                            "|nom.co" +

                            "|com.es" +
                            "|nom.es" +
                            "|org.es" +

                            "|co.in" +
                            "|firm.in" +
                            "|gen.in" +
                            "|ind.in" +
                            "|net.in" +
                            "|org.in" +

                            "|com.mx" +

                            "|co.nz" +
                            "|net.nz" +
                            "|org.nz" +

                            "|com.tw" +
                            "|idv.tw" +
                            "|org.tw" +

                            "|co.uk" +
                            "|me.uk" +
                            "|org.uk" +

                            "|gov.cn" +
                            "|co.jp" +
                            "|com" +
                            "|co" +
                            "|info" +
                            "|net" +
                            "|org" +
                            "|me" +
                            "|mobi" +
                            "|us" +
                            "|biz" +
                            "|xxx" +
                            "|ca" +

                            "|cn" +

                            "|mx" +
                            "|tv" +
                            "|ws" +
                            "|ag" +

                            "|am" +
                            "|asia" +
                            "|at" +
                            "|be" +
                            "|cc" +
                            "|de" +
                            "|es" +
                            "|eu" +
                            "|fm" +
                            "|fr" +
                            "|gs" +
                            "|in" +
                            "|it" +
                            "|jobs" +
                            "|jp" +
                            "|ms" +
                            "|bz" +

                            "|nl" +
                            "|nu" +
                            "|hk" +
                            "|vg" +
                            "|se" +
                            "|tc" +
                            "|tk" +
                            "|tw" +
                            "|gov" +
                            "|la" +
                            "|club";
}
