package app.haiyunshan.whatsnote.setting.item;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.remote.RemoteManager;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import club.andnext.download.DownloadAgent;
import club.andnext.download.DownloadEntity;

public class UpdateSettingItem extends BaseSettingItem {

    UpdateEntity entity;

    public UpdateSettingItem(@NonNull UpdateEntity entity) {
        this.entity = entity;
    }

    public UpdateEntity getEntity() {
        return entity;
    }



}
