package app.haiyunshan.whatsnote.setting.viewholder;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;

import java.util.function.Consumer;

public class MasterSettingViewHolder extends ProfileSettingViewHolder {

    public static final int LAYOUT_RESOURCE_ID = R.layout.layout_setting_master_list_item;

    View iconLayout;

    @Keep
    public MasterSettingViewHolder(View itemView) {
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

        this.iconLayout = view.findViewById(R.id.icon_layout);
        iconLayout.setClipToOutline(true);

        iconLayout.setOnClickListener(this::onPortraitClick);
    }

    void onPortraitClick(View view) {
        Consumer consumer = getItem().getConsumer();
        if (consumer != null) {
            consumer.accept(getItem().getUserObject());
        }
    }

    @Override
    CharSequence getText(CharSequence input) {
        CharSequence text = input;
        if (TextUtils.isEmpty(text)) {
            text = "个性签名";
        }

        return text;
    }

}
