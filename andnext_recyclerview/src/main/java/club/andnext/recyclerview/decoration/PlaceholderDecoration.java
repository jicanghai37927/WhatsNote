package club.andnext.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.R;

public class PlaceholderDecoration extends RecyclerView.ItemDecoration {

    Drawable divider;

    int margin;
    Rect bounds;
    
    public PlaceholderDecoration(Context context) {
        this.divider = context.getDrawable(R.drawable.anc_shape_list_divider);

        this.bounds = new Rect();

    }

    public PlaceholderDecoration setMargin(int value) {
        this.margin = value;

        return this;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        if (parent.getLayoutManager() == null || divider == null) {
            return;
        }

        canvas.save();

        final int left;
        final int right;

        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int x = left + margin;
        int y = 0;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, bounds);

            final int bottom = bounds.bottom;
            y = (y < bottom)? bottom: y;
        }

        if (childCount != 0) {
            int height = parent.getChildAt(0).getHeight();
            int count = (parent.getHeight() - y) / height;

            y += height;
            for (int i = 0; i < count; i++) {
                divider.setBounds(x, y, right, y + divider.getIntrinsicHeight());
                divider.draw(canvas);

                y += height; 
            }
        }

        canvas.restore();
    }


}
