package app.haiyunshan.whatsnote.setting.item;

import android.net.Uri;
import androidx.annotation.DrawableRes;

public class PictureSettingItem extends BaseSettingItem {

    Uri iconUri;

    @DrawableRes int placeholderResId;
    @DrawableRes int errorResId;

    int targetWidth;
    int targetHeight;

    public PictureSettingItem(Uri iconUri, int targetWidth, int targetHeight) {
        this.iconUri = iconUri;
        this.placeholderResId = 0;
        this.errorResId = 0;

        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public Uri getIconUri() {
        return iconUri;
    }

    public PictureSettingItem setIconUri(Uri iconUri) {
        this.iconUri = iconUri;

        return this;
    }

    public int getPlaceholderResId() {
        return placeholderResId;
    }

    public PictureSettingItem setPlaceholderResId(@DrawableRes int placeholderResId) {
        this.placeholderResId = placeholderResId;

        return this;
    }

    public int getErrorResId() {
        return errorResId;
    }

    public PictureSettingItem setErrorResId(@DrawableRes int errorResId) {
        this.errorResId = errorResId;

        return this;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }
}
