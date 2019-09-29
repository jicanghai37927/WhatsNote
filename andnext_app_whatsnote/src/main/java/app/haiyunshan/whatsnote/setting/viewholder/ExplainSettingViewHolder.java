package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.ExplainSettingItem;

public class ExplainSettingViewHolder extends BaseSettingViewHolder<ExplainSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_explain_list_item;

    @Keep
    public ExplainSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        view.setOnClickListener(null);
        view.setClickable(false);
    }
}
