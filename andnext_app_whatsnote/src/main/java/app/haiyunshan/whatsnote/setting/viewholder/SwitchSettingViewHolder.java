package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.SwitchSettingItem;

import java.util.function.Consumer;

public class SwitchSettingViewHolder extends BaseSettingViewHolder<SwitchSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_switch_list_item;

    Switch switchView;

    @Keep
    public SwitchSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        {
            view.setOnClickListener(null);
            view.setClickable(false);
        }

        {
            this.switchView = view.findViewById(R.id.sc_switch);

        }
    }

    @Override
    public void onBind(SwitchSettingItem item, int position) {
        super.onBind(item, position);

        switchView.setOnCheckedChangeListener(null);

        switchView.setChecked(item.isChecked());

        switchView.setOnCheckedChangeListener(this::onCheckedChanged);
    }

    void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SwitchSettingItem item = getItem();
        if (item != null) {
            Consumer<Boolean> consumer = item.getConsumer();
            if (consumer != null) {
                consumer.accept(isChecked);
            }
        }
    }

}
