package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.article.PictureGalleryFragment;
import app.haiyunshan.whatsnote.base.BaseActivity;

public class PictureGalleryActivity extends BaseActivity {

    public static final void startForResult(Fragment f, String documentId, String pictureId, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, PictureGalleryActivity.class);
        intent.putExtra("documentId", documentId);
        intent.putExtra("pictureId", pictureId);

        f.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return PictureGalleryFragment.class;
    }
}
