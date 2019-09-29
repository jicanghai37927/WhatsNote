package club.andnext.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

/**
 *
 */
public class PackageUtils {

    public static final boolean pick(Activity context, Fragment fragment,
                                     String type, String[] mimeTypes,
                                     boolean documentOnly, int requestCode) {

        boolean result = false;

        if (context == null && fragment == null) {
            return false;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(Intent.ACTION_OPEN_DOCUMENT); // 红米6Pro不支持视频，只能以文件方式选择视频
        list.add(Intent.ACTION_GET_CONTENT); // 坚果手机2会使用SmartianOS文件浏览器，而不是Android的文件选择器

        if (!documentOnly) {
            list.add(0, Intent.ACTION_PICK);
        }

        for (String action : list) {
            if (action.equalsIgnoreCase(Intent.ACTION_PICK)) {
                if (mimeTypes != null && mimeTypes.length > 1) {
                    continue;
                }
            }

            Intent intent = new Intent();

            intent.setAction(action);
            intent.setType(type);

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);

            if (mimeTypes != null && mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            if (action.equalsIgnoreCase(Intent.ACTION_OPEN_DOCUMENT)
                    || action.equalsIgnoreCase(Intent.ACTION_GET_CONTENT)) {
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }

            try {

                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode);
                } else {
                    context.startActivityForResult(intent, requestCode);
                }

                result = true;

                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static final boolean startBrowser(Context context, Uri uri) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, null);

        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static final boolean openImage(Context context, Uri uri) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //注意加上这句话

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean showMarket(Context context) {
        return showMarket(context, null, context.getPackageName());
    }

    public static boolean showMarket(Context context, String marketPkg) {
        return showMarket(context, marketPkg, context.getPackageName());
    }

    public static boolean showMarket(Context context, String marketPkg, String appPkg) {
        if (TextUtils.isEmpty(appPkg)) {
            return false;
        }

        try {
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static final boolean install(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        String mimeType = "application/vnd.android.package-archive";

        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = UriUtils.fromFile(context, file);

            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, mimeType);

        } else {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static int getVersionCode(Context ctx) {
        int localVersion = 0;

        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);

            localVersion = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return localVersion;
    }


    /**
     * 获取本地软件版本号名称
     */
    public static String getVersionName(Context ctx) {
        String localVersion = "";

        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);

            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return localVersion;
    }

    public static Drawable getIcon(@NonNull Context context, @NonNull String pkgName) {

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            if (info != null) {
                return info.loadIcon(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
