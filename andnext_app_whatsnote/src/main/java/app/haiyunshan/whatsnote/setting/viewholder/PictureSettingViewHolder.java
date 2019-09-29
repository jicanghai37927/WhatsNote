package app.haiyunshan.whatsnote.setting.viewholder;

import android.net.Uri;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.PictureSettingItem;
import club.andnext.widget.TargetSizeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PictureSettingViewHolder extends BaseSettingViewHolder<PictureSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_picture_list_item;

    protected TargetSizeImageView iconView;

    @Keep
    public PictureSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        this.iconView = view.findViewById(R.id.iv_icon);
    }

    @Override
    public void onBind(PictureSettingItem item, int position) {

        {
            iconView.setTargetSize(item.getTargetWidth(), item.getTargetHeight());
        }

        Uri uri = item.getIconUri();
        int placeholder = item.getPlaceholderResId();
        int error = item.getErrorResId();

        if (uri == null) {
            int resId = (placeholder == 0)? error: placeholder;
            iconView.setImageResource(resId);
        } else {
            RequestOptions options = new RequestOptions();
            if (placeholder != 0) {
                options = options.placeholder(placeholder);
            }
            if (error != 0) {
                options = options.error(error);
            }

            Glide.with(iconView)
                    .load(uri)
                    .apply(options)
                    .into(iconView);
        }
    }
}
