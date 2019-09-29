package app.haiyunshan.whatsnote.article.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import club.andnext.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareIntentAction extends SharePictureAction {

    ResolveInfo resolveInfo;

    public ShareIntentAction(Context context, View view, RecordEntity entity, Document document, ResolveInfo resolveInfo) {
        super(context, view, entity, document);

        this.resolveInfo = resolveInfo;

        this.requestMsg = "请允许访问设备上的内容，以共享笔记。";
        this.deniedMsg = "请在「权限管理」，设置允许「读写手机存储」，以共享笔记。";
    }

    @Override
    void onNext(SharePictureAction action) {

        // getHint bitmap
        Bitmap bitmap = action.apply();

        // save bitmap to pictures
        final Uri uri = action.accept(bitmap);

        // recycle bitmap
        if (bitmap != null) {
            bitmap.recycle();
        }

        // start intent
        this.accept(uri);
    }

    @Override
    void accept(Uri uri) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        boolean hasImage = (uri != null);
        String text = getText(document, getFooter()).toString();

        // mime-type
        {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (!hasImage) {
                intent.setType("text/plain");
            } else {
                if (hasImage) {
                    hasImage = isSupport(context, resolveInfo, intent);
                    if (!hasImage) {
                        intent.setType("text/plain");
                    }
                }
            }
        }

        // component
        {
            String pkg = resolveInfo.activityInfo.packageName;
            String cls = resolveInfo.activityInfo.name;
            ComponentName cn = new ComponentName(pkg, cls);
            intent.setComponent(cn);
        }

        // text
        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }

        // picture
        if (hasImage) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND)) {

                intent.removeExtra(Intent.EXTRA_STREAM);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }

            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND_MULTIPLE)) {

                ArrayList<Uri> imageUris = new ArrayList();
                imageUris.add(uri); // Add your image URIs here

                intent.removeExtra(Intent.EXTRA_STREAM);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }
        }

        // start
        try {
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    @Override
    public String getFooter() {
        String footer = "";

        File file = Directory.get(context, Directory.DIR_FOOTER);
        file = new File(file, "share.txt");
        if (file.exists()) {
            try {
                footer = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return footer;
    }

    static final boolean isSupport(Context context, ResolveInfo resolveInfo, Intent intent) {

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (list == null || list.isEmpty()) {
            return false;
        }

        for (ResolveInfo entry : list) {
            if (entry.activityInfo.packageName.equalsIgnoreCase(resolveInfo.activityInfo.packageName)
                    && entry.activityInfo.name.equalsIgnoreCase(resolveInfo.activityInfo.name)) {
                return true;
            }
        }

        return false;
    }
}
