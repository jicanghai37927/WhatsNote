package club.andnext.helper;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class PermissionHelper {

    String mPermission;

    CharSequence mRequestMsg;
    CharSequence mDeniedMsg;

    FragmentActivity mContext;

    OnPermissionListener mOnPermissionListener;

    public PermissionHelper(FragmentActivity context) {
        this.mContext = context;

        this.mPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        this.mRequestMsg = "";
        this.mDeniedMsg  = "";
    }

    public PermissionHelper setPermission(String permission) {
        this.mPermission = permission;

        return this;
    }

    public PermissionHelper setRequestMessage(CharSequence text) {
        this.mRequestMsg = text;

        return this;
    }

    public PermissionHelper setDeniedMessage(CharSequence text) {
        this.mDeniedMsg = text;

        return this;
    }

    public PermissionHelper setOnPermissionListener(OnPermissionListener listener) {
        this.mOnPermissionListener = listener;

        return this;
    }

    public boolean isGranted() {
        boolean isMarshmallow = Build.VERSION.SDK_INT >= 23;
        if (!isMarshmallow) {
            return true;
        }

        boolean result = mContext.checkSelfPermission(mPermission) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    public void request() {
        RxPermissions rxPermission = new RxPermissions(mContext);

        rxPermission.requestEach(mPermission).subscribe(this::accept);
    }

    void accept(Permission permission) {

        if (permission.granted) {

            // 用户已经同意该权限
            if (mOnPermissionListener != null) {
                mOnPermissionListener.onPermissionGranted(this);
            }

        } else if (permission.shouldShowRequestPermissionRationale) {

            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框

            DialogInterface.OnClickListener listener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    request();
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                }

            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(mRequestMsg);
            builder.setPositiveButton(android.R.string.yes, listener);
            builder.setNegativeButton(android.R.string.cancel, listener);

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        } else { // 用户拒绝了该权限，并且选中『不再询问』

            DialogInterface.OnClickListener listener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    showSetting(mContext);
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                }

            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(mDeniedMsg);
            builder.setPositiveButton(android.R.string.yes, listener);
            builder.setNegativeButton(android.R.string.cancel, listener);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    static void showSetting(Context context) {

        if (gotoMiuiPermission(context)) {
            return;
        }

        if (gotoMeizuPermission(context)) {
            return;
        }

        if (gotoHuaweiPermission(context)) {
            return;
        }

        if (gotoAppDetailSetting(context)) {
            return;
        }
    }

    static boolean gotoMiuiPermission(Context context) {

        // MIUI 8

        try {

            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);

            return true;

        } catch (Exception e) {


        }

        // MIUI 5/6/7

        try {

            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);

            return true;

        } catch (Exception e1) {

        }

        return false;
    }

    static boolean gotoMeizuPermission(Context context) {

        try {

            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    static boolean gotoHuaweiPermission(Context context) {

        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return false;
    }

    static boolean gotoAppDetailSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }

        try {
            context.startActivity(localIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *
     */
    public interface OnPermissionListener {

        void onPermissionGranted(PermissionHelper helper);
    }
}
