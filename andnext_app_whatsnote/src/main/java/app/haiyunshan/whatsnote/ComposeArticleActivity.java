package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.article.ComposeArticleFragment;
import app.haiyunshan.whatsnote.base.BaseActivity;

public class ComposeArticleActivity extends BaseActivity {

    public static final void startForResult(Fragment f, String id, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, ComposeArticleActivity.class);
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
        return ComposeArticleFragment.class;
    }

    public void requestCompose(String id) {

        Fragment f = createFragment();
        if (f != null) {

            Bundle args = getIntent().getExtras();
            args.putString("id", id);
            f.setArguments(args);

            this.replace(f);
        }
    }
}
