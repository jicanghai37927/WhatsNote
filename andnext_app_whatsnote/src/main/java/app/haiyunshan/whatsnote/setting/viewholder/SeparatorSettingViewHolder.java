package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.setting.item.SeparatorSettingItem;

public class SeparatorSettingViewHolder extends BaseSettingViewHolder<SeparatorSettingItem> {

    @Keep
    public SeparatorSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        {
            view.setOnClickListener(null);
            view.setClickable(false);

            view.setMinimumHeight(0);
            view.setBackground(null);
        }

        if (iconView != null) {
            iconView.setVisibility(View.GONE);
        }

        if (nameView != null) {
            nameView.setVisibility(View.GONE);
        }

        if (chevronView != null) {
            chevronView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBind(SeparatorSettingItem item, int position) {
        super.onBind(item, position);

        {
            itemView.getLayoutParams().height = item.getHeight();
            itemView.requestLayout();
        }
    }
}
