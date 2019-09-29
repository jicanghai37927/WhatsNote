package app.haiyunshan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import androidx.appcompat.widget.AppCompatEditText;

public class TestEdit extends AppCompatEditText {

    public TestEdit(Context context) {
        super(context);
    }

    public TestEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {

        Log.w("AA", "onKeyShortcut");

        return super.onKeyShortcut(keyCode, event);
    }
}
