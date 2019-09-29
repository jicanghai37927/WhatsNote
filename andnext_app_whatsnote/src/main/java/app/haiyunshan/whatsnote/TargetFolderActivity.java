package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.record.TargetFolderFragment;

public class TargetFolderActivity extends BaseActivity {

    public static final void start(Fragment f, String id) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, TargetFolderActivity.class);
        intent.putExtra("id", id);

        f.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return TargetFolderFragment.class;
    }
}
