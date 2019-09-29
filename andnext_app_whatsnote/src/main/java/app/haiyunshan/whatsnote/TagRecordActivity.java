package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.record.BaseRecordListFragment;
import app.haiyunshan.whatsnote.record.TagRecordFragment;

public class TagRecordActivity extends BaseActivity {

    public static final void start(Fragment f, String id) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, TagRecordActivity.class);
        intent.putExtra("id", id);

        f.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Class<? extends BaseRecordListFragment> getFragmentKind() {
        return TagRecordFragment.class;
    }
}
