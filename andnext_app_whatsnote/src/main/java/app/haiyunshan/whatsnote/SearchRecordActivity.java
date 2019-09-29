package app.haiyunshan.whatsnote;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import androidx.fragment.app.Fragment;
        import app.haiyunshan.whatsnote.base.BaseActivity;
        import app.haiyunshan.whatsnote.record.SearchRecordFragment;

public class SearchRecordActivity extends BaseActivity {

    public static final void start(Fragment f) {
        Activity context = f.getActivity();

        Intent intent = new Intent(context, SearchRecordActivity.class);

        f.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Class<? extends SearchRecordFragment> getFragmentKind() {
        return SearchRecordFragment.class;
    }
}
