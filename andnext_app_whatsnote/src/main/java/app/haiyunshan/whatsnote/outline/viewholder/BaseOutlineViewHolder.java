package app.haiyunshan.whatsnote.outline.viewholder;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.outline.entity.BaseOutlineEntity;
import app.haiyunshan.whatsnote.outline.helper.OutlineHelper;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.DeleteRunner;

import java.util.Arrays;
import java.util.List;

public class BaseOutlineViewHolder<T extends BaseOutlineEntity> extends SwipeViewHolder<T> {

    public static final int LAYOUT_RES_ID = R.layout.layout_outline_list_item;

    ImageView checkBox;
    ImageView iconView;
    ViewStub viewStub;
    ImageView dragView;

    OutlineHelper helper;

    @Keep
    public BaseOutlineViewHolder(OutlineHelper helper, View itemView) {
        super(itemView);

        this.helper = helper;
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view) {

        {
            view.setOnClickListener(this::onItemClick);

            this.checkBox = view.findViewById(R.id.iv_checkbox);
            this.iconView = view.findViewById(R.id.iv_icon);
            this.viewStub = view.findViewById(R.id.stub);

            this.dragView = view.findViewById(R.id.btn_drag);
            dragView.setOnTouchListener(this::onDragTouch);
        }

        {
            SwipeHolder swipeHolder = new SwipeHolder(helper.getSwipeHelper(), view, view.findViewById(R.id.swipe_view));

            {
                View btnAction = view.findViewById(R.id.btn_delete);
                btnAction.setOnClickListener(this::onDeleteClick);

                DeleteRunner r = new DeleteRunner();
                r.add(btnAction);

                swipeHolder.add(r);
            }

            this.setSwipeHolder(swipeHolder);
        }
    }

    @CallSuper
    @Override
    public void onBind(T item, int position) {
        super.onBind(item, position);

        this.updateCheck(item);

        if (item == helper.getExpand()) {
            getSwipeHolder().expand(SwipeActionHelper.DIRECTION_RTL, false);
        }
    }

    @Override
    public T getItem() {
        return super.getItem();
    }

    @Override
    public List<View> getTouchable(SwipeActionHelper helper) {
        return Arrays.asList(itemView.findViewById(R.id.swipe_view));
    }

    @Override
    public void onActionEnd(SwipeActionHelper helper, int action) {
        if (action == SwipeActionHelper.ACTION_RIGHT) {
            T item = getItem();
            if (item != null) {
                this.helper.remove(this);
            }
        }
    }

    void onItemClick(View view) {
        BaseOutlineEntity item = this.getItem();
        boolean checked = helper.isChecked(item);
        checked = !checked;
        helper.setChecked(item, checked);

        this.updateCheck(item);
    }

    boolean onDragTouch(View view, MotionEvent event) {
        final boolean enable = itemView.isEnabled();

        itemView.setClickable(false);
        itemView.post(() -> itemView.setClickable(enable));

        helper.requestDrag(this);

        return false;
    }

    void onDeleteClick(View view) {
        helper.remove(this);
    }

    void updateCheck(BaseOutlineEntity item) {
        boolean checked = helper.isChecked(item);
        if (checked) {
            checkBox.setImageResource(R.drawable.ic_check_circle_white_24dp);
        } else {
            checkBox.setImageDrawable(null);
        }
    }
}
