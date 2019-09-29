package club.andnext.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class KeyboardDrawer extends FrameLayout {

    public KeyboardDrawer(@NonNull Context context) {
        this(context, null);
    }

    public KeyboardDrawer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public KeyboardDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_MOVE) {
            boolean hide = ev.getY() > this.getHeight();
            if (hide) {
                hideSoftInput(getContext());
            }
        }

        return super.dispatchTouchEvent(ev);
    }


    /**
     *
     * @param context
     */
    private static final void hideSoftInput(Context context) {
        if (!(context instanceof Activity)) {
            return;
        }

        View view = ((Activity)context).getCurrentFocus();

        if (view == null) {
            return;
        }

        InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
