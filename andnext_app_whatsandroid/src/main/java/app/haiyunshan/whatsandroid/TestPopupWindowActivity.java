package app.haiyunshan.whatsandroid;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.PopupWindow;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestPopupWindowActivity extends AppCompatActivity {

    TapeHelper tapeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_popup_window);

        {
            ToggleButton button = findViewById(R.id.btn_toggle);

            final View layout = findViewById(R.id.layout_more);
            layout.setVisibility(button.isChecked()? View.VISIBLE: View.GONE);

            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                layout.setVisibility(isChecked? View.VISIBLE: View.GONE);
            });
        }

        {
            View button = findViewById(R.id.btn_tape);
            button.setOnLongClickListener(this::onTapeLongClick);
        }

        {
            this.tapeHelper = new TapeHelper(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);

        tapeHelper.setTouchEvent(ev);
        if (tapeHelper.interceptTouchEvent) {
            tapeHelper.dispatchTouchEvent();
        }

        return result;
    }

    boolean onTapeLongClick(View view) {

        tapeHelper.show();

        tapeHelper.interceptTouchEvent = true;

        view.post(() -> tapeHelper.dispatchTouchEvent());

        return true;
    }

    /**
     *
     */
    private static class TapeHelper {

        boolean interceptTouchEvent;
        int statusBarHeight;
        MotionEvent touchEvent;

        View tapeLayout;

        View view;

        PopupWindow popupWindow;

        TestPopupWindowActivity parent;

        TapeHelper(TestPopupWindowActivity parent) {
            this.parent = parent;

            this.view = parent.getLayoutInflater().inflate(R.layout.layout_test_tape_editor, null);
            this.tapeLayout = view.findViewById(R.id.action_tape);

            this.popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.setFocusable(true);
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setTouchInterceptor(this::onTouch);
        }

        public boolean dispatchTouchEvent() {
            touchEvent.offsetLocation(0, -statusBarHeight);
            MotionEvent ev = touchEvent;



            if (tapeLayout.getWidth() <= 0 || tapeLayout.getHeight() <= 0) {

            } else {

                View view = tapeLayout.getRootView();

                Rect rect = new Rect();
                rect.set(0, 0, tapeLayout.getWidth(), tapeLayout.getHeight());

                ((ViewGroup) view).offsetDescendantRectToMyCoords(tapeLayout, rect);
                ev.offsetLocation(-rect.left, -rect.top);


                tapeLayout.dispatchTouchEvent(ev);
            }


            Log.w("AA", "dispatchTouch x = " + ev.getX() + ", y = " + ev.getY());



                switch (ev.getActionMasked()) {
                    case MotionEvent.ACTION_UP: {
                        interceptTouchEvent = false;

                        break;
                    }
                }

                return true;

        }

        void setTouchEvent(MotionEvent ev) {
            if (touchEvent != null) {
                touchEvent.recycle();
            }
            touchEvent = MotionEvent.obtain(ev);
        }

        boolean onTouch(View v, MotionEvent ev) {
            Log.w("AA", "onTouch x = " + ev.getX() + ", y = " + ev.getY());

            int action = ev.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                View view = tapeLayout.getRootView();

                Rect rect = new Rect();
                rect.set(0, 0, tapeLayout.getWidth(), tapeLayout.getHeight());
                ((ViewGroup) view).offsetDescendantRectToMyCoords(tapeLayout, rect);

                if (!rect.contains((int)(ev.getX()), (int)(ev.getY()))) {
                    popupWindow.dismiss();
                    return true;
                }
            } else if (action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow.dismiss();
                return true;
            }

            return false;
        }

        void show() {
            if (popupWindow.isShowing()) {
                return;
            }

            View anchor = parent.findViewById(R.id.btn_tape);
            final Rect visibleDisplayFrame = new Rect();
            anchor.getRootView().getWindowVisibleDisplayFrame(visibleDisplayFrame);
            this.statusBarHeight = visibleDisplayFrame.top;
            Log.w("AA", "statusBarHeight = " + statusBarHeight);

            view.setPadding(0, 0, 0, getPaddingBottom(anchor));
            popupWindow.showAtLocation(anchor, Gravity.BOTTOM|Gravity.RIGHT, 0, 0);
        }


        int getPaddingBottom(View anchor) {

            int y;

            View rootView = anchor.getRootView();
            final Rect visibleDisplayFrame = new Rect();

            // root view relative to display
            {
                DisplayMetrics dm = new DisplayMetrics();
                Display d = parent.getWindowManager().getDefaultDisplay();
                d.getMetrics(dm);
                int realHeight = dm.heightPixels;

                rootView.getWindowVisibleDisplayFrame(visibleDisplayFrame);

                y = realHeight - visibleDisplayFrame.bottom;
            }

            // anchor relative to root
            if (rootView instanceof ViewGroup) {
                final Rect anchorRect = new Rect(0, 0, anchor.getWidth(), anchor.getHeight());
                ViewGroup layout = (ViewGroup)rootView;
                layout.offsetDescendantRectToMyCoords(anchor, anchorRect);

                y += (visibleDisplayFrame.bottom - anchorRect.bottom);
            }

            return y;
        }

        int getY(View anchor) {

            int y;

            View rootView = anchor.getRootView();
            final Rect visibleDisplayFrame = new Rect();

            // root view relative to display
            {
                DisplayMetrics dm = new DisplayMetrics();
                Display d = parent.getWindowManager().getDefaultDisplay();
                d.getRealMetrics(dm);
                int realHeight = dm.heightPixels;

                rootView.getWindowVisibleDisplayFrame(visibleDisplayFrame);

                y = realHeight - visibleDisplayFrame.bottom;
            }

            // anchor relative to root
            if (rootView instanceof ViewGroup) {
                final Rect anchorRect = new Rect(0, 0, anchor.getWidth(), anchor.getHeight());
                ViewGroup layout = (ViewGroup)rootView;
                layout.offsetDescendantRectToMyCoords(anchor, anchorRect);

                y += (visibleDisplayFrame.bottom - anchorRect.bottom);
            }

            return y;
        }

        boolean isInputMethodVisible(View anchor) {

            DisplayMetrics dm = new DisplayMetrics();
            Display d = parent.getWindowManager().getDefaultDisplay();
            d.getMetrics(dm);
            int visibleHeight = dm.heightPixels;

            final Rect visibleDisplayFrame = new Rect();
            anchor.getRootView().getWindowVisibleDisplayFrame(visibleDisplayFrame);

            boolean isKeyboardVisible = (visibleHeight != visibleDisplayFrame.bottom);
            return isKeyboardVisible;
        }
    }
}
