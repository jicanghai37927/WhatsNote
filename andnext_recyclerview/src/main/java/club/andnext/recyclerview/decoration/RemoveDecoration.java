package club.andnext.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RemoveDecoration extends RecyclerView.ItemDecoration {

    ArrayList<RemoveInfo> list;

    public RemoveDecoration(Context context) {
        this.list = new ArrayList<>();
    }

    public void add(Drawable d, long duration) {
        this.list.add(new RemoveInfo(d, duration));
    }

    public void add(Drawable d, long duration, boolean scale) {
        this.list.add(new RemoveInfo(d, duration, scale));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        {
            drawVertical(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    void drawVertical(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        if (parent.getLayoutManager() == null) {
            return;
        }

        canvas.save();

        {
            final int left;
            final int right;

            //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
            if (parent.getClipToPadding()) {
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                canvas.clipRect(left, parent.getPaddingTop(), right,
                        parent.getHeight() - parent.getPaddingBottom());
            }
        }

        {
            long time = System.currentTimeMillis();
            for (int i = list.size() - 1; i >= 0; i--) {
                RemoveInfo info = list.get(i);
                if (time >= info.getExpire()) {
                    list.remove(i);
                }
            }
        }

        {
            boolean invalidate = false;

            for (RemoveInfo info: list) {
                invalidate |= info.update();

                info.drawable.draw(canvas);
            }

            if (invalidate) {
                parent.invalidate();
            }
        }

        canvas.restore();
    }

    /**
     *
     */
    private static class RemoveInfo {

        Drawable drawable;
        long startTime;
        long duration;
        long delay;

        Rect bounds;
        boolean scale;

        RemoveInfo(Drawable d, long duration) {
            this(d, duration, false);
        }

        RemoveInfo(Drawable d, long duration, boolean scale) {
            this.drawable = d;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
            this.delay = 100;

            this.scale = scale;
            this.bounds = new Rect(d.getBounds());
        }

        long getExpire() {
            return startTime + duration + delay;
        }

        boolean update() {
            if (!scale) {
                return false;
            }

            long now = System.currentTimeMillis();
            long expire = this.getExpire();
            if (now >= expire) {
                return false;
            }

            int height = bounds.height();

            long remain = expire - now;
            if (remain < duration) {
                height = (int)(bounds.height() * remain / duration);
            }

            drawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.top + height);
            return true;

        }
    }
}
