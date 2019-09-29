package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.BaseSettingItem;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.SectionDividerDecoration;

import java.util.function.Supplier;

public class BaseSettingViewHolder<T extends BaseSettingItem> extends BridgeViewHolder<T> implements SectionDividerDecoration.Adapter {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_list_item;

    ImageView iconView;
    TextView nameView;
    TextView textView;
    TextView badgeView;
    ImageView chevronView;

    @Keep
    public BaseSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        view.setOnClickListener(this::onItemClick);

        this.iconView = view.findViewById(R.id.iv_icon);
        this.nameView = view.findViewById(R.id.tv_name);
        this.textView = view.findViewById(R.id.tv_text);
        this.badgeView = view.findViewById(R.id.tv_badge);
        this.chevronView = view.findViewById(R.id.iv_chevron);

        if (iconView != null) {
            if (iconView.getOutlineProvider() != null) {
                iconView.setClipToOutline(true);
            }
        }
    }

    @Override
    public void onBind(T item, int position) {

        if (iconView != null) {
            if (item.getIcon() > 0) {
                iconView.setImageResource(item.getIcon());
                iconView.setVisibility(View.VISIBLE);
            } else {
                iconView.setImageDrawable(null);
                iconView.setVisibility(View.GONE);
            }

            iconView.setImageTintList(item.getIconTintList());
        }

        if (nameView != null) {
            nameView.setText(item.getName());

            if (item.getButtonColor() != null) {
                nameView.setTextColor(item.getButtonColor());
            }
        }

        if (textView != null) {

            Supplier<CharSequence> s = item.getHintSupplier();
            CharSequence hint = (s == null)? item.getHint(): s.get();
            textView.setHint(hint);
        }

        if (badgeView != null) {
            Supplier<Integer> s = item.getBadgeSupplier();
            int badge = (s == null)? item.getBadge(): s.get();

            badgeView.setText(String.valueOf(badge));
            badgeView.setVisibility(badge > 0? View.VISIBLE: View.GONE);
        }

        if (chevronView != null) {
            chevronView.setVisibility(item.getChevron());
        }

    }

    protected void onItemClick(View view) {
        T item = getItem();
        if (item.getConsumer() != null) {
            item.getConsumer().accept(item.getUserObject());
        }
    }

    @Override
    public int getMargin(SectionDividerDecoration decoration) {
        int margin = 0;

        int type = getItem().getDividerType();
        if (type == BaseSettingItem.DIVIDER_NAME) {
            if (nameView != null) {
                margin = nameView.getLeft();
            }
        }

        return margin;
    }

    @Override
    public boolean isVisible(SectionDividerDecoration decoration) {
        if (getItem() == null) {
            return true;
        }

        return getItem().isDividerVisible();
    }
}
