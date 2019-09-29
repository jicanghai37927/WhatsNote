package club.andnext.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.R;

public class MarginDividerDecoration extends RecyclerView.ItemDecoration {

    Drawable background;
    Drawable divider;

    Rect bounds;
    int margin;

    boolean drawOver;
    boolean drawTop;
    boolean drawBottom;
    boolean lastMargin = false;
    boolean alphaEnable = true;

    RecyclerView.ViewHolder dragViewHolder;

    public MarginDividerDecoration(Context context) {

        this.divider = context.getDrawable(R.drawable.anc_shape_list_divider);

        this.bounds = new Rect();
        this.margin = 0;

        this.drawOver = true;
        this.drawTop = true;
        this.drawBottom = true;

    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setDivider(Drawable d) {
        this.divider = d;
    }

    public void setMargin(int value) {
        this.margin = value;
    }

    public void setDrawOver(boolean value) {
        this.drawOver = value;
    }

    public void setDrawTop(boolean value) {
        this.drawTop = value;
    }

    public void setDrawBottom(boolean value) {
        this.drawBottom = value;
    }

    public void setLastMargin(boolean value) {
        this.lastMargin = value;
    }

    public void setAlphaEnable(boolean value) {
        this.alphaEnable = value;
    }

    public void setDragViewHolder(RecyclerView.ViewHolder holder) {
        this.dragViewHolder = holder;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if (!drawOver) {
            drawVertical(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (drawOver) {
            drawVertical(c, parent, state);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (divider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        if (drawOver) {
            outRect.set(0, 0, 0, 0);
        } else {
            if (parent.findContainingViewHolder(view).getAdapterPosition() == 0 && this.drawTop) {
                outRect.set(0, divider.getIntrinsicHeight(), 0, divider.getIntrinsicHeight());
            } else {
                int bottom = (view.getVisibility() == View.GONE)? 0: divider.getIntrinsicHeight();

                outRect.set(0, 0, 0, bottom);
            }


        }
    }

    void drawVertical(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        if (parent.getLayoutManager() == null || divider == null) {
            return;
        }

        if (parent.getAdapter() == null) {
            return;
        }

        canvas.save();

        final int itemCount = parent.getAdapter().getItemCount();
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

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.ViewHolder h = parent.findContainingViewHolder(child);

            // in drag, don't draw divider
            if (h == this.dragViewHolder) {
                continue;
            }

            if (alphaEnable) {
                float alpha = this.getAlpha(parent, i);
                divider.setAlpha((int) (0xff * alpha));
            }

            parent.getDecoratedBoundsWithMargins(child, bounds);
            final int bottom = bounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - divider.getIntrinsicHeight();

            int pos = parent.getChildAdapterPosition(child);

            // 列表最顶部
            if (pos == 0 && drawTop) {
                divider.setBounds(left, bounds.top, right, bounds.top + divider.getIntrinsicHeight());
                divider.draw(canvas);
            }

            // 列表最底部
            if ((pos + 1) == itemCount && !drawBottom) {
                continue;
            }

            // background
            if (background != null) {
                background.setBounds(left, top, right, bottom);
                background.draw(canvas);
            }

            // divider
            if (isVisible(h)) {
                boolean isLast = (pos + 1 == itemCount);

                int offset = (!isLast || lastMargin)? (int)this.getTranslation(h): 0;

                int x = left;
                x += (!isLast || lastMargin)? this.getMargin(h): 0;

                divider.setBounds(x + offset, top, right + offset, bottom);
                divider.draw(canvas);
            }

        }

        canvas.restore();
    }

    float getAlpha(RecyclerView recyclerView, int index) {
        View view = recyclerView.getChildAt(index);
        float a = view.getAlpha();

        float b = ((index + 1) >= recyclerView.getChildCount())? 1: (recyclerView.getChildAt(index + 1)).getAlpha();

        return Math.min(a, b);
    }

    float getTranslation(RecyclerView.ViewHolder holder) {

        if (holder instanceof Adapter) {
            return ((Adapter)holder).getTranslation(this);
        }

        if (holder.getAdapterPosition() < 0) {
            return holder.itemView.getWidth();
        }

        return 0;
    }

    int getMargin(RecyclerView.ViewHolder holder) {
        return this.margin;
    }

    boolean isVisible(RecyclerView.ViewHolder holder) {
        return true;
    }

    /**
     *
     */
    public interface Adapter {

        float getTranslation(MarginDividerDecoration decoration);

    }
}
