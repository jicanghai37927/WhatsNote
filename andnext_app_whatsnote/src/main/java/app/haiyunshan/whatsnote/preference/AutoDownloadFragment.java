package app.haiyunshan.whatsnote.preference;


import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.preference.entity.PreferenceEntity;
import app.haiyunshan.whatsnote.setting.BasePreferenceFragment;
import app.haiyunshan.whatsnote.setting.item.BaseSettingItem;
import app.haiyunshan.whatsnote.setting.item.ExplainSettingItem;
import app.haiyunshan.whatsnote.setting.item.SwitchSettingItem;
import app.haiyunshan.whatsnote.setting.viewholder.BaseSettingViewHolder;
import app.haiyunshan.whatsnote.setting.viewholder.ExplainSettingViewHolder;
import app.haiyunshan.whatsnote.setting.viewholder.SwitchSettingViewHolder;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoDownloadFragment extends BasePreferenceFragment {

    public AutoDownloadFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            titleBar.setTitle("自动下载");
        }
    }

    @Override
    protected List<? extends BaseSettingItem> createList() {
        ArrayList<BaseSettingItem> list = new ArrayList<>();

        {
            PreferenceEntity pref = PreferenceEntity.obtain();

            SwitchSettingItem item = new SwitchSettingItem();
            item.setName("自动下载");
            item.setChecked(pref.isAutoDownload());
            item.setChevron(View.GONE);
            item.setConsumer((checked) -> {

                pref.setAutoDownload(checked);
                pref.save();

            });

            list.add(item);
        }

        {
            CharSequence hint = "接入无线局域网时，自动下载新版本安装包。";

            ExplainSettingItem item = new ExplainSettingItem(hint);
            item.setDividerVisible(false);

            list.add(item);
        }

        return list;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {

        adapter.bind(SwitchSettingItem.class,
                new BridgeBuilder(SwitchSettingViewHolder.class, SwitchSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(ExplainSettingItem.class,
                new BridgeBuilder(ExplainSettingViewHolder.class, ExplainSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(BaseSettingItem.class,
                new BridgeBuilder(BaseSettingViewHolder.class, BaseSettingViewHolder.LAYOUT_RES_ID));
    }


}
