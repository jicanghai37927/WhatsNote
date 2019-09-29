package club.andnext.recyclerview.swipe;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

public class ForbidTouchListener implements RecyclerView.OnItemTouchListener {

    boolean forbidden;
    long expire;

    public ForbidTouchListener() {
        this.forbidden = false;
        this.expire = -1;
    }

    public void setForbidden(boolean value) {
        this.forbidden = value;
        this.expire = -1;
    }

    public void setForbidden(boolean value, long milliseconds) {
        this.forbidden = value;
        if (milliseconds <= 0) {
            this.expire = -1;
        } else {
            this.expire = System.currentTimeMillis() + milliseconds;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (forbidden) {

            if (expire <= 0) { // 永不过期
                return true;
            }

            if (System.currentTimeMillis() < expire) { // 有效时间内，禁止操作
                return true;
            }
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}