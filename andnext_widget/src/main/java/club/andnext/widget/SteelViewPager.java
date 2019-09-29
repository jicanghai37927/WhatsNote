package club.andnext.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class SteelViewPager extends ViewPager {

    public SteelViewPager(@NonNull Context context) {
        super(context);
    }

    public SteelViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        return super.onTouchEvent(ev);

        return false;
    }
}
