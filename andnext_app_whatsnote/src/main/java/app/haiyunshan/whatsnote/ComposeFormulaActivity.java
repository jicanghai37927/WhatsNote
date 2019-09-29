package app.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.BaseActivity;
import app.haiyunshan.whatsnote.formula.ComposeFormulaFragment;

public class ComposeFormulaActivity extends BaseActivity {

    public static final void startForResult(Fragment f, String formula, int requestCode) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, ComposeFormulaActivity.class);
        if (!TextUtils.isEmpty(formula)) {
            intent.putExtra("formula", formula);
        }

        f.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> getFragmentKind() {
        return ComposeFormulaFragment.class;
    }
}
