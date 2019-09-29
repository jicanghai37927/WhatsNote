package app.haiyunshan.whatsnote.record.viewholder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.tag.widget.TagView;
import app.haiyunshan.whatsnote.widget.RecordIconView;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.DeleteRunner;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

public abstract class RecordViewHolder extends SwipeViewHolder<RecordEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_record_list_item;

    View contentLayout;

    RecordIconView iconView;
    TextView nameView;
    TextView infoView;
    TagView tagView;

    Callback callback;

    @Keep
    public RecordViewHolder(Callback callback, View itemView) {
        super(itemView);

        this.callback = callback;
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view) {

        {
            this.contentLayout = view.findViewById(R.id.content_layout);
            contentLayout.setOnClickListener(this::onItemClick);
            contentLayout.setOnLongClickListener(this::onItemLongClick);

            this.iconView = view.findViewById(R.id.iv_icon);
            this.nameView = view.findViewById(R.id.tv_name);
            this.infoView = view.findViewById(R.id.tv_info);
            this.tagView = view.findViewById(R.id.tag_view);
        }

        {
            SwipeHolder swipeHolder = new SwipeHolder(callback.getSwipeHelper(), view, view.findViewById(R.id.content_layout));

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

    @Override
    public void onBind(RecordEntity item, int position) {
        super.onBind(item, position);

        {
            iconView.setIcon(item);
        }

        {
            nameView.setText(item.getName());
        }

        {
            StringBuilder sb = new StringBuilder();

            sb.append(formatTime(item));
            sb.append(" - ");
            sb.append(formatSize(item));

            infoView.setHint(sb);
        }

        {
            tagView.setTarget(item);
        }
    }

    @Override
    public void onActionEnd(SwipeActionHelper helper, int action) {
        if (action == SwipeActionHelper.ACTION_RIGHT) {
            RecordEntity item = getItem();
            if (item != null) {
                this.callback.onDelete(this, item);
            }
        }
    }
    void onDeleteClick(View view) {
        this.callback.onDelete(this, getItem());
    }

    void onItemClick(View v) {
        RecordEntity e = this.getItem();
        if (e != null) {
            callback.onItemClick(getItem());
        }
    }

    boolean onItemLongClick(View v) {
        RecordEntity e = this.getItem();
        if (e != null) {
            return callback.onItemLongClick(this, getItem());
        }

        return false;
    }

    CharSequence formatTime(RecordEntity item) {
        DateTime dateTime = callback.getTime(item);

        StringBuilder sb = new StringBuilder();

        int days = Days.daysBetween(dateTime.toLocalDate(), LocalDate.now()).getDays();
        if (days == 0) {

            sb.append(String.format("%02d", dateTime.getHourOfDay()));
            sb.append(':');
            sb.append(String.format("%02d", dateTime.getMinuteOfHour()));

        } else if (days == 1) {
            sb.append("昨天");
        } else if (days == 2) {
            sb.append("前天");
        } else {
            sb.append(dateTime.getYear());
            sb.append('/');
            sb.append(dateTime.getMonthOfYear());
            sb.append('/');
            sb.append(dateTime.getDayOfMonth());
        }

        return sb;
    }

    CharSequence formatSize(RecordEntity item) {
        return "";
    }

    /**
     *
     */
    public static abstract class Callback {

        protected abstract SwipeActionHelper getSwipeHelper();

        protected abstract DateTime getTime(RecordEntity item);

        protected abstract void onDelete(RecordViewHolder viewHolder, RecordEntity item);

        protected abstract void onItemClick(RecordEntity item);

        protected abstract boolean onItemLongClick(RecordViewHolder viewHolder, RecordEntity item);
    }
}
