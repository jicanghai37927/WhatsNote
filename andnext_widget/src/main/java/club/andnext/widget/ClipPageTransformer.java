package club.andnext.widget;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class ClipPageTransformer implements ViewPager.PageTransformer {

    Rect clipRect;
    int gutterWidth;

    public ClipPageTransformer(int gutterWidth) {
        this.clipRect = new Rect();
        this.gutterWidth = gutterWidth;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position >= 1 || position <= -1) {
            page.setClipBounds(null);
        } else if (position < 0) {

            int width = (int)(position * gutterWidth);
            clipRect.set(0, 0, page.getWidth() + width, page.getHeight());
            page.setClipBounds(clipRect);

        } else if (position < 1) {

            int width = (int)(position * gutterWidth);
            clipRect.set(width, 0, page.getWidth(), page.getHeight());
            page.setClipBounds(clipRect);

        }
    }
}