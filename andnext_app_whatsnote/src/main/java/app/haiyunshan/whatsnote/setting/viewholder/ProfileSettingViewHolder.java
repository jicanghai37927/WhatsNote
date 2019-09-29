package app.haiyunshan.whatsnote.setting.viewholder;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.ProfileSettingItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ProfileSettingViewHolder extends BaseSettingViewHolder<ProfileSettingItem> {

    public static final int LAYOUT_RESOURCE_ID = R.layout.layout_setting_profile_list_item;

    @Keep
    public ProfileSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RESOURCE_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
    }

    @Override
    public void onBind(ProfileSettingItem item, int position) {

        {
            iconView.setImageResource(R.drawable.ic_portrait);
        }

        {
            CharSequence name = getName(item.getName());
            nameView.setText(name);
        }

        {
            CharSequence text = getText(item.getHint());
            textView.setText(text);
        }

        if (item.getIconUri() != null) {
            Glide.with(iconView)
                    .load(item.getIconUri())
                    .apply(RequestOptions.errorOf(R.drawable.ic_portrait).placeholder(R.drawable.ic_portrait))
                    .into(iconView);
        }
    }

    CharSequence getName(CharSequence input) {
        CharSequence name = input;
        name = (TextUtils.isEmpty(name))? "佚名" : name;

        return name;
    }

    CharSequence getText(CharSequence input) {
        CharSequence text = input;
        if (TextUtils.isEmpty(text)) {
            text = "个性签名 与 个性图签";
        } else {
            String str = text.toString().trim();
            int pos = str.indexOf('\n');
            if (pos > 0) {
                text = str.substring(0, pos) + " ...";
            } else {
                text = str;
            }
        }

        return text;
    }
}
