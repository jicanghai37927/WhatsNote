package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.general.PhotoViewerFragment;

public class PhotoViewerActivity extends BaseActivity {

    public static final void startForResult(Fragment f, int requestCode,
                                            Uri uri) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.setType("image/*");

        PhotoViewerFragment.PhotoExtra extra = new PhotoViewerFragment.PhotoExtra(uri.toString(), -1, -1, "");
        ArrayList<PhotoViewerFragment.PhotoExtra> list = new ArrayList<>();
        list.add(extra);

        intent.putParcelableArrayListExtra("list", list);

        f.startActivityForResult(intent, requestCode);
    }

    public static final void start(Context context, Uri uri, int width, int height, String signature) {

        PhotoViewerFragment.PhotoExtra extra = new PhotoViewerFragment.PhotoExtra(uri.toString(), width, height, signature);
        ArrayList<PhotoViewerFragment.PhotoExtra> list = new ArrayList<>();
        list.add(extra);

        start(context, 0, list);
    }

    public static final void start(Context context, int position, ArrayList<PhotoViewerFragment.PhotoExtra> list) {

        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.setType("image/*");

        intent.putExtra("position", position);
        intent.putParcelableArrayListExtra("list", list);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return PhotoViewerFragment.class;
    }
}
