package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.IconSettingItem;

public class IconSettingViewHolder extends BaseSettingViewHolder<IconSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_icon_list_item;

    @Keep
    public IconSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        view.setClickable(false);
    }
}
