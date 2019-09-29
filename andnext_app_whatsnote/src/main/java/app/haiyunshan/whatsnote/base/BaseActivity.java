package app.haiyunshan.whatsnote.base;

import android.os.Bundle;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String CONTENT = "content";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment f = createFragment();
        if (f != null) {

            f.setArguments(getIntent().getExtras());

            this.replace(f);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        {
            FragmentManager fm = getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag(CONTENT);
            if (f != null && f instanceof OnRestartListener) {
                ((OnRestartListener)f).onRestart();
            }
        }

    }

    @Override
    public void onBackPressed() {

        {
            FragmentManager fm = getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag(CONTENT);
            if (f != null && f instanceof OnBackPressedListener) {
                boolean result = ((OnBackPressedListener)f).onBackPressed();
                if (result) {
                    return;
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    protected Fragment createFragment() {

        Fragment f = null;

        try {
            Class<? extends Fragment> kind = getFragmentKind();
            f = kind.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return f;

    }

    protected void replace(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(android.R.id.content, f, CONTENT);
        t.commit();
    }

    protected abstract @NonNull Class<? extends Fragment> getFragmentKind();

}
