package app.haiyunshan.whatsnote.setting.viewholder;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.CaptionSettingItem;

import java.util.function.Supplier;

public class CaptionSettingViewHolder extends BaseSettingViewHolder<CaptionSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_caption_list_item;

    @Keep
    public CaptionSettingViewHolder(View itemView) {
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
    }

    @Override
    public void onBind(CaptionSettingItem item, int position) {
        super.onBind(item, position);

        if (nameView != null) {
            nameView.setTextAppearance(item.getAppearance());
            nameView.setMinLines(item.getMinLines());

            int top = nameView.getPaddingTop();
            top = (item.getPaddingTop() >= 0)? item.getPaddingTop(): top;

            int bottom = nameView.getPaddingBottom();
            bottom = (item.getPaddingBottom() >= 0)? item.getPaddingBottom(): bottom;

            int left = nameView.getPaddingLeft();
            int right = nameView.getPaddingRight();

            nameView.setPadding(left, top, right, bottom);
        }

        if (nameView != null) {
            if (TextUtils.isEmpty(nameView.getText())) {

                Supplier<CharSequence> s = item.getHintSupplier();
                CharSequence hint = (s == null)? item.getHint(): s.get();
                nameView.setHint(hint);
            }
        }
    }
}
