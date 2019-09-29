package app.haiyunshan.whatsnote.outline.helper;

import android.util.ArraySet;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.outline.entity.BaseOutlineEntity;
import app.haiyunshan.whatsnote.outline.entity.Outline;
import app.haiyunshan.whatsnote.outline.viewholder.BaseOutlineViewHolder;
import club.andnext.recyclerview.swipe.SwipeActionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OutlineHelper {

    Set<BaseOutlineEntity> checkedSet;

    BaseOutlineEntity expand;

    Callback callback;

    public OutlineHelper(@NonNull Callback callback) {
        this.checkedSet = new ArraySet<>();

        this.expand = null;
        this.callback = callback;
    }

    public List<BaseOutlineEntity> getCheckedList() {
        return new ArrayList<>(checkedSet);
    }

    public boolean isChecked(BaseOutlineEntity entity) {
        return checkedSet.contains(entity);
    }

    public void setChecked(BaseOutlineEntity entity, boolean value) {
        if (value) {
            checkedSet.add(entity);
        } else {
            checkedSet.remove(entity);
        }

        {
            callback.onCheckedChanged(this);
        }
    }

    public int getCheckedCount() {
        return checkedSet.size();
    }

    public void setChecked(boolean value) {
        checkedSet.clear();
        if (value) {
            checkedSet.addAll(callback.getOutline().getList());
        }

        {
            callback.onCheckedChanged(this);
        }
    }

    public void setExpand(BaseOutlineEntity entity) {
        if (entity == expand) {
            return;
        }

        this.expand = entity;
    }

    public BaseOutlineEntity getExpand() {
        return this.expand;
    }

    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        callback.requestDrag(viewHolder);
    }

    public SwipeActionHelper getSwipeHelper() {
        return callback.getSwipeHelper();
    }

    public void remove(BaseOutlineViewHolder viewHolder) {
        callback.requestRemove(viewHolder);
    }

    /**
     *
     */
    public interface Callback {

        Outline getOutline();

        SwipeActionHelper getSwipeHelper();

        void requestRemove(BaseOutlineViewHolder viewHolder);

        void requestDrag(RecyclerView.ViewHolder viewHolder);

        void onCheckedChanged(OutlineHelper helper);
    }
}
