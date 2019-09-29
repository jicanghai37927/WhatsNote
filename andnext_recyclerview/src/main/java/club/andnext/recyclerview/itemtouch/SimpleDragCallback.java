package club.andnext.recyclerview.itemtouch;

import android.graphics.Canvas;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.R;

public abstract class SimpleDragCallback extends ItemTouchHelper.SimpleCallback {

    protected float elevation;

    public SimpleDragCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);

        this.elevation = 12.f;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return super.getMoveThreshold(viewHolder);
    }

    @CallSuper
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        // order attention
        {
            View view = viewHolder.itemView;

            final Object tag = view.getTag(R.id.anc_item_drag_previous_elevation);
            if (tag != null && tag instanceof Float) {
                ViewCompat.setElevation(view, (Float) tag);
            }

            view.setTag(R.id.anc_item_drag_previous_elevation, null);
        }

        {
            super.clearView(recyclerView, viewHolder);
        }

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return super.getMovementFlags(recyclerView, viewHolder);
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return super.canDropOver(recyclerView, current, target);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        // order attention
        {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        if (isCurrentlyActive) {
            View view = viewHolder.itemView;

            Object originalElevation = view.getTag(R.id.anc_item_drag_previous_elevation);
            if (originalElevation == null) {
                originalElevation = ViewCompat.getElevation(view);

                float newElevation = elevation + findMaxElevation(recyclerView, view);
                ViewCompat.setElevation(view, newElevation);

                view.setTag(R.id.anc_item_drag_previous_elevation, originalElevation);
            }
        }

    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    private float findMaxElevation(RecyclerView recyclerView, View itemView) {
        final int childCount = recyclerView.getChildCount();
        float max = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = recyclerView.getChildAt(i);
            if (child == itemView) {
                continue;
            }
            final float elevation = ViewCompat.getElevation(child);
            if (elevation > max) {
                max = elevation;
            }
        }
        return max;
    }
}
