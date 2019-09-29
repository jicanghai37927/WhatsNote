package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.article.ShareArticleFragment;
import app.haiyunshan.whatsnote.base.BaseActivity;

public class ShareArticleActivity extends BaseActivity {

    public static final void startForResult(Fragment f, String id, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, ShareArticleActivity.class);
        intent.putExtra("id", id);

        f.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return ShareArticleFragment.class;
    }
}
