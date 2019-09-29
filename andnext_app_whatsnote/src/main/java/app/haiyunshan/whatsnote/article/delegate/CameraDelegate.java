package app.haiyunshan.whatsnote.article.delegate;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.article.insertion.BaseInsertion;
import app.haiyunshan.whatsnote.article.insertion.CameraInsertion;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import club.andnext.utils.SoftInputUtils;
import club.andnext.utils.UriUtils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class CameraDelegate extends BaseRequestDelegate {

    PictureEntity target;

    BaseInsertion.Callback callback;

    public CameraDelegate(Fragment f, BaseInsertion.Callback callback) {
        super(f);

        this.callback = callback;
        this.target = PictureEntity.create(callback.getDocument());
    }

    @Override
    public boolean request() {
        SoftInputUtils.hide(context);

        Uri imageUri;
        {
            File file = target.getFile();
            file.getAbsoluteFile().getParentFile().mkdirs();

            imageUri = UriUtils.fromFile(context, file);
        }

        Intent intent = new Intent();
        {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }

        try {

            parent.startActivityForResult(intent, getRequestCode());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void accept(Integer resultCode, Intent data) {

        if ((resultCode != RESULT_OK)) {
            return;
        }

        if (target.getFile().exists()) {
            new CameraInsertion(callback, target).execute();
        }
    }

}
