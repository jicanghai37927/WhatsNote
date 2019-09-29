package app.haiyunshan.whatsnote.setting.item;

import android.net.Uri;
import android.view.View;
import app.haiyunshan.whatsnote.preference.entity.ProfileEntity;

public class ProfileSettingItem extends BaseSettingItem {

    Uri iconUri;

    public ProfileSettingItem(ProfileEntity entity) {
        this(entity.getPortraitUri(), entity.getName(), entity.getSignature());
    }

    public ProfileSettingItem(Uri iconUri, CharSequence name, CharSequence text) {
        this.iconUri = iconUri;
        this.name = name;
        this.hint = text;

        this.chevron = View.VISIBLE;
    }

    public Uri getIconUri() {
        return iconUri;
    }

    public ProfileSettingItem setIconUri(Uri uri) {
        this.iconUri = uri;

        return this;
    }
}
