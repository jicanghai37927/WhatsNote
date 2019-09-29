package app.haiyunshan.whatsnote.article.share;

import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import club.andnext.snapshot.Snapshot;
import club.andnext.utils.FileUtils;
import com.google.android.material.snackbar.Snackbar;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SharePictureAction extends BaseShareFileAction {

    public SharePictureAction(Context context, View view, RecordEntity entity, Document document) {
        super(context, view, entity, document);

        {
            this.requestMsg = "请允许访问设备上的内容，以保存图片。";
            this.deniedMsg = "请在「权限管理」，设置允许「读写手机存储」，以保存图片。";
        }

        {
            String name = context.getString(R.string.share_directory);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name);
            file.mkdirs();

            this.targetDir = file;
        }
    }

    @Override
    void accept() {
        Observable.just(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onNext);
    }

    @Override
    public String getFooter() {
        String footer = "";

        return footer;
    }

    void onNext(SharePictureAction action) {

        // getHint bitmap
        Bitmap bitmap = action.apply();

        // save bitmap to pictures
        final Uri uri = action.accept(bitmap);

        // recycle bitmap
        if (bitmap != null) {
            bitmap.recycle();
        }

        // showMessage notify to user
        if (uri != null) {

            Snackbar snackbar;

            {
                CharSequence text = String.format("已保存到 相册/%1$s。", context.getString(R.string.share_directory));
                snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
            }

            {
                CharSequence text = "查看";
                View.OnClickListener listener = view -> accept(uri);

                snackbar.setAction(text, listener);
            }

            snackbar.show();

        } else {

        }
    }

    void accept(Uri uri) {
        start(context, uri, "image/*");
    }

    Uri accept(Bitmap bitmap) {
        Uri url = null;

        if (bitmap == null) {
            return url;
        }

        File file = this.targetDir;
        file = new File(file, getFileName(entity) + ".jpg");

        // removeFromRecord previous bitmap
        {
            deleteImage(context, file);
        }

        // save file
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();

            file.delete();
            file = null;
        }

        // update media store
        if (file != null && file.exists()) {
            String title = entity.getName();
            String description = "";
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            url = insertImage(context, file, title, description, width, height);
        }

        return url;
    }

    Bitmap apply() {
        Snapshot shot = new Snapshot(view);
        return shot.apply();
    }

    static final Uri insertImage(Context context,
                                  File file,
                                  String title,
                                  String description,
                                  int width,
                                  int height) {
        Uri url;

        ContentValues values = new ContentValues();
        ContentResolver cr = context.getContentResolver();

        // insert to media store
        {
            long time = file.lastModified();

            // media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
            // for DATE_TAKEN
            long dateSeconds = time / 1000;

            // mime-type
            String mimeType = "image/jpeg";

            values.put(MediaStore.Images.ImageColumns.TITLE, title);
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title);
            values.put(MediaStore.Images.ImageColumns.DESCRIPTION, description);

            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.Images.ImageColumns.WIDTH, width);
            values.put(MediaStore.Images.ImageColumns.HEIGHT, height);
            values.put(MediaStore.Images.ImageColumns.SIZE, file.length());
            values.put(MediaStore.Images.ImageColumns.DATA, file.getAbsolutePath());

            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, time);
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);

            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        // generate thumbnail
        {
            long id = ContentUris.parseId(url);

            // Wait until MINI_KIND thumbnail is generated.
            Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (miniThumb != null) {
                miniThumb.recycle();
            }
        }

        return url;
    }

    static final int deleteImage(Context context, File file) {

        int count = 0;
        ContentResolver resolver = context.getContentResolver();

        try {

            // 删除旧文件
            count = resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns.DATA + "=?",
                    new String[] { file.getAbsolutePath() });

        } catch (Exception e) {

        }

        return count;
    }
}
