package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.tag.TagFragment;

public class TagActivity extends BaseActivity {

    public static final void start(Fragment f, String id) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, TagActivity.class);
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
        return TagFragment.class;
    }
}
