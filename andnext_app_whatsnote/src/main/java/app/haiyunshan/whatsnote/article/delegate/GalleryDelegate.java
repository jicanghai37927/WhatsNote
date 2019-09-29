package app.haiyunshan.whatsnote.article.delegate;

import android.content.Intent;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.PictureGalleryActivity;
import app.haiyunshan.whatsnote.article.helper.ComposeHelper;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import club.andnext.utils.SoftInputUtils;

import static android.app.Activity.RESULT_OK;

public class GalleryDelegate extends BaseRequestDelegate {

    String documentId;
    String pictureId;

    ComposeHelper helper;

    public GalleryDelegate(Fragment f, ComposeHelper helper, String documentId, String pictureId) {
        super(f);

        this.helper = helper;
        this.documentId = documentId;
        this.pictureId = pictureId;
    }

    @Override
    public boolean request() {
        SoftInputUtils.hide(context);

        try {

            PictureGalleryActivity.startForResult(parent, documentId, pictureId, requestCode);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void accept(Integer resultCode, Intent data) {

        if ((resultCode != RESULT_OK) || (data == null)) {
            return;
        }

        String id = data.getStringExtra("id");
        if (TextUtils.isEmpty(id)) {
            return;
        }

        helper.scrollTo(id);
    }

}
