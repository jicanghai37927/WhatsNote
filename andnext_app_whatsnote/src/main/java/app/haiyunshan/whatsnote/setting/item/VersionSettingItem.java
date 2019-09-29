package app.haiyunshan.whatsnote.setting.item;

import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.update.UpdateEntity;

public class VersionSettingItem extends BaseSettingItem {

    UpdateEntity entity;

    public VersionSettingItem(@NonNull UpdateEntity entity) {
        this.entity = entity;

        this.iconResId = R.mipmap.ic_launcher_foreground;
    }

    public UpdateEntity getEntity() {
        return entity;
    }
}
