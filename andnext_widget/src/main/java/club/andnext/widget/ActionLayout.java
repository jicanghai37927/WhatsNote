package club.andnext.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class ActionLayout extends LinearLayout {

    private static final String TAG = "ActionLayout";

    View actionView;
    View.OnClickListener onClickListener;

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    public ActionLayout(Context context) {
        this(context, null);
    }

    public ActionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.onClickListener = l;

        super.setOnClickListener(l);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = ev.getPointerId(0);

                this.updateAction(ev);

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    mActivePointerId = ev.getPointerId(0);
                }

                this.updateAction(ev);

                break;
            }
            case MotionEvent.ACTION_CANCEL: {

                this.mActivePointerId = INVALID_POINTER;
                this.actionView = null;

                break;
            }

            case MotionEvent.ACTION_UP: {

                this.fireAction();

                this.mActivePointerId = INVALID_POINTER;
                this.actionView = null;

                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);

                this.updateAction(ev);

                break;
            }
        }

        return isClickable() || (getChildCount() != 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int actionMasked = ev.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = ev.getPointerId(0);

                updateAction(ev);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    mActivePointerId = ev.getPointerId(0);
                }

                updateAction(ev);

                break;
            }

            case MotionEvent.ACTION_UP: {

                fireAction();

                this.mActivePointerId = INVALID_POINTER;
                this.actionView = null;

                break;
            }

            case MotionEvent.ACTION_CANCEL: {

                mActivePointerId = INVALID_POINTER;
                this.actionView = null;

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                this.mActivePointerId = ev.getPointerId(index);

                this.updateAction(ev);

                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);

                this.updateAction(ev);

                break;
            }
        }

        return isClickable() || (getChildCount() != 0);
    }

    void fireAction() {
        if (actionView == null) {
            return;
        }

        actionView.setPressed(false);
        if (onClickListener != null) {
            onClickListener.onClick(actionView);
        }
    }

    void updateAction(MotionEvent ev) {
        final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
        if (activePointerIndex == -1) {
            Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
            return;
        }

        final float x = ev.getX(activePointerIndex);
        final float y = ev.getY(activePointerIndex);

        View view = findChildViewUnder(x, y);
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view = null;
        }

        if (actionView == view) {
            return;
        }

        if (actionView != null) {
            actionView.setPressed(false);
        }

        if (view != null) {
            view.setPressed(true);
        }

        this.actionView = view;
    }

    View findChildViewUnder(float x, float y) {
        final int count = this.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = this.getChildAt(i);

            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();
            if (x >= child.getLeft() + translationX
                    && x <= child.getRight() + translationX
                    && y >= child.getTop() + translationY
                    && y <= child.getBottom() + translationY) {
                return child;
            }
        }

        return null;
    }

    void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }
}
