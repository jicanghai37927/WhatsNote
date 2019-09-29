package app.haiyunshan.whatsnote.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

public class NativeHelper {

    public static final boolean checkValid(Context context, boolean notify) {
        return true;
    }

    public static final String getCypher(Context context) {
        return "http://andnext.club/whatsnote";
    }

    public static final String stringFromJNI(Context context) {
        return "http://andnext.club/whatsnote";
    }

    public static String getSignature(Context context) {

        try {
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;

            /******* 循环遍历签名数组拼接应用签名 *******/
            return signatures[0].toCharsString();

            /************** 得到应用签名 **************/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
