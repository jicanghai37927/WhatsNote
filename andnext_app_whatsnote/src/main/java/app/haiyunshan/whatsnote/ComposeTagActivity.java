package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.tag.CreateTagFragment;
import app.haiyunshan.whatsnote.tag.EditTagFragment;

public class ComposeTagActivity extends BaseActivity {

    public static final void startForResult(Fragment f, String id, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, ComposeTagActivity.class);
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
        Intent intent = this.getIntent();
        String id = intent.getStringExtra("id");
        if (TextUtils.isEmpty(id)) {
            return CreateTagFragment.class;
        }

        return EditTagFragment.class;
    }
}
