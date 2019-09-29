package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.preference.AutoDownloadFragment;

public class AutoDownloadActivity extends BaseActivity {

    public static final void startForResult(Fragment f, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, AutoDownloadActivity.class);

        f.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return AutoDownloadFragment.class;
    }
}
