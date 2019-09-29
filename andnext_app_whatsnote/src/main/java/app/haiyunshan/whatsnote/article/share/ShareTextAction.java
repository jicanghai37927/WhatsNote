package app.haiyunshan.whatsnote.article.share;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import club.andnext.utils.FileUtils;
import club.andnext.utils.UriUtils;
import com.google.android.material.snackbar.Snackbar;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;

public class ShareTextAction extends BaseShareFileAction {

    public ShareTextAction(Context context, View view, RecordEntity entity, Document document) {
        super(context, view, entity, document);

        {
            this.requestMsg = "请允许访问设备上的内容，以保存笔记。";
            this.deniedMsg = "请在「权限管理」，设置允许「读写手机存储」，以保存笔记。";
        }

        {
            String name = context.getString(R.string.share_directory);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
            file = new File(file, "Text");
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

        File file = Directory.get(context, Directory.DIR_FOOTER);
        file = new File(file, "text.txt");
        if (file.exists()) {
            try {
                footer = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return footer;
    }

    void onNext(ShareTextAction action) {

        // save file
        final File file = action.apply();

        // showMessage notify to user
        if (file != null) {

            Snackbar snackbar;

            {
                CharSequence text = String.format("已保存到 文档/%1$s。", context.getString(R.string.share_directory));
                snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
            }

            {
                CharSequence text = "查看";
                View.OnClickListener listener = view -> accept(file);

                snackbar.setAction(text, listener);
            }

            snackbar.show();

        } else {

        }
    }

    File apply() {

        File file = this.targetDir;

        {
            file = new File(file, getFileName(entity) + ".txt");
        }

        {
            String text = getText(document, getFooter()).toString();
            file = writeStringToFile(file, text);
        }

        return file;

    }

    void accept(File file) {
        start(context, UriUtils.fromFile(context, file), "text/plain");

    }

}
