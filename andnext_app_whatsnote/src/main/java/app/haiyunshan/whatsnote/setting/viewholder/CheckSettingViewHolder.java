package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.CheckSettingItem;

public class CheckSettingViewHolder extends BaseSettingViewHolder<CheckSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_check_list_item;

    ImageView checkView;

    @Keep
    public CheckSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        this.checkView = view.findViewById(R.id.iv_check);
    }

    @Override
    public void onBind(CheckSettingItem item, int position) {
        super.onBind(item, position);

        checkView.setVisibility(item.isChecked()? View.VISIBLE: View.INVISIBLE);
    }
}
