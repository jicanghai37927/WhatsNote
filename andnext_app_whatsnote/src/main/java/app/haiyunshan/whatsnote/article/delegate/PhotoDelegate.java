package app.haiyunshan.whatsnote.article.delegate;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.article.insertion.BaseInsertion;
import app.haiyunshan.whatsnote.article.insertion.PictureInsertion;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import club.andnext.utils.SoftInputUtils;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class PhotoDelegate extends BaseRequestDelegate {

    BaseInsertion.Callback callback;

    public PhotoDelegate(Fragment f, BaseInsertion.Callback callback) {
        super(f);

        this.callback = callback;
    }

    @Override
    public boolean request() {
        SoftInputUtils.hide(context);

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        try {
            parent.startActivityForResult(intent, getRequestCode());

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    @Override
    public void accept(Integer resultCode, Intent data) {

        if ((resultCode != RESULT_OK) || (data == null)) {
            return;
        }

        ArrayList<Uri> list = new ArrayList<>();

        {
            ClipData clipData = data.getClipData();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    list.add(uri);
                }
            }

            if (list.isEmpty()) {
                Uri uri = data.getData();
                if (uri != null) {
                    list.add(uri);
                }
            }
        }

        {
            new PictureInsertion(callback, list).execute();
        }

    }

}
