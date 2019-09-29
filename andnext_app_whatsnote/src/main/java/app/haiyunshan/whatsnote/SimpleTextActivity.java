package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.setting.SimpleTextFragment;

public class SimpleTextActivity extends BaseActivity {

    public static final int TYPE_PLAIN  = SimpleTextFragment.TYPE_PLAIN;
    public static final int TYPE_MD     = SimpleTextFragment.TYPE_MARKDOWN;
    public static final int TYPE_HTML   = SimpleTextFragment.TYPE_HTML;

    public static final void start(Fragment f, String title, String text, int type) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, SimpleTextActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("type", type);

        f.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return SimpleTextFragment.class;
    }
}
