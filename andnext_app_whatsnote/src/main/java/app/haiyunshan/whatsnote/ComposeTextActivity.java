package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.setting.ComposeTextFragment;

public class ComposeTextActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return ComposeTextFragment.class;
    }

    /**
     *
     */
    public static class Builder extends ComposeTextFragment.Builder {

        public Builder(@NonNull CharSequence title, @NonNull CharSequence text) {
            super(title, text);
        }

        public void start(@NonNull Fragment fragment, int requestCode) {
            Activity activity = fragment.getActivity();
            fragment.startActivityForResult(getIntent(activity), requestCode);
        }

        public Intent getIntent(@NonNull Context context) {
            intent.setClass(context, ComposeTextActivity.class);
            intent.putExtras(bundle);
            return intent;
        }

    }

    /**
     *
     */
    public static class Options extends ComposeTextFragment.Options {

    }
}
