package club.andnext.recyclerview.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class BackgroundDrawable extends Drawable {

    Drawable background;

    int offset;
    int range;

    RecyclerView recyclerView;

    public BackgroundDrawable(RecyclerView view, Drawable d) {
        this.recyclerView = view;
        this.background = d;

        view.addOnScrollListener(scrollListener);
    }

    public void setBackground(Drawable d) {
        this.background = d;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (this.background == null) {
            return;
        }

        {
            updateOffsetAndRange(recyclerView);
        }

        {
            canvas.save();
            canvas.translate(0, -offset);

            background.draw(canvas);

            canvas.restore();
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    void updateOffsetAndRange(RecyclerView parent) {

        this.offset = parent.computeVerticalScrollOffset();
        this.range = parent.computeVerticalScrollRange();

        if (parent.getChildCount() != 0) {
            int index = parent.getChildCount() - 1;
            View v = parent.getChildAt(index);
            int bottom = v.getBottom();

            bottom += offset;
            bottom += parent.getPaddingBottom();

            range = (range < bottom)? bottom: range;
        }

        range = (range < parent.getHeight())? parent.getHeight(): range;

        int width = parent.getWidth();
        int height = background.getBounds().height();
        if (height != range) {
            background.setBounds(0, 0, width, range);
        }
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            updateOffsetAndRange(recyclerView);
        }
    };
}
