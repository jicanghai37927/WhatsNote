package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.about.AboutFragment;
import app.haiyunshan.whatsnote.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    public static final void start(Fragment f) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, AboutActivity.class);

        f.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return AboutFragment.class;
    }
}
