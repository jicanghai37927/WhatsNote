package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.TitleSettingItem;

public class TitleSettingViewHolder extends BaseSettingViewHolder<TitleSettingItem> {

    public static final int LAYOUT_RESOURCE_ID = R.layout.layout_setting_title_list_item;

    @Keep
    public TitleSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RESOURCE_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        view.setOnClickListener(null);
        view.setClickable(false);
    }

}
