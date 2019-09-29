package app.haiyunshan.whatsnote.preference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.*;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.RequestResultDelegate;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.preference.entity.PreferenceEntity;
import app.haiyunshan.whatsnote.preference.entity.ProfileEntity;
import app.haiyunshan.whatsnote.setting.BasePreferenceFragment;
import app.haiyunshan.whatsnote.setting.item.BaseSettingItem;
import app.haiyunshan.whatsnote.setting.item.ExplainSettingItem;
import app.haiyunshan.whatsnote.setting.item.ProfileSettingItem;
import app.haiyunshan.whatsnote.setting.viewholder.BaseSettingViewHolder;
import app.haiyunshan.whatsnote.setting.viewholder.ExplainSettingViewHolder;
import app.haiyunshan.whatsnote.setting.viewholder.ProfileSettingViewHolder;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.utils.PackageUtils;
import club.andnext.utils.WebUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SettingFragment extends BasePreferenceFragment {

    static final String ID_PROFILE  = "profile";
    static final String ID_OFFICIAL = "official";
    static final String ID_UPDATE   = "update";

    RequestResultManager requestResultManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestResultManager = new RequestResultManager();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected List<BaseSettingItem> createList() {
        List<BaseSettingItem> list = new ArrayList<>();

        {
            ProfileSettingItem item = new ProfileSettingItem(PreferenceEntity.obtain().getProfile());
            item.setId(ID_PROFILE);

            item.setConsumer((obj) -> {
                RequestProfileDelegate delegate = new RequestProfileDelegate(this, (data) -> onProfileChanged());

                requestResultManager.request(delegate);
            });

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        boolean officialEnable = false;
        if (officialEnable) {

        }

        {

            final UpdateEntity entity = UpdateEntity.obtain();

            BaseSettingItem item = new BaseSettingItem("软件更新");
            item.setId(ID_UPDATE);
            item.setIcon(R.drawable.ic_update_white_24dp);
            item.setIconTintList(getActivity(), R.color.colorIcon);
            item.setChevron(View.VISIBLE);
            item.setBadge(entity.exist()? 1: 0);

            item.setConsumer((obj) -> {
                RequestResultDelegate delegate = new RequestResultDelegate(this,
                        ((f, c) -> {
                            UpdateActivity.startForResult(f, c);
                            return true;
                        }),
                        (r, i) -> {
                            item.setBadge(entity.exist()? 1: 0);
                            notifyItemChanged(getItem(ID_UPDATE));
                        });

                requestResultManager.request(delegate);
            });

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem("帮助和反馈");
            item.setIcon(R.drawable.ic_help_outline_white_24dp);
            item.setIconTintList(getActivity(), R.color.colorIcon);
            item.setChevron(View.INVISIBLE);
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);

            item.setConsumer((obj) -> {
                String url = "http://andnext.club/whatsnote/help.html";
                url = WebUtils.withTimestamp(url);

                PackageUtils.startBrowser(getActivity(), Uri.parse(url));

            });

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem("关于神马笔记");
            item.setIcon(R.drawable.ic_info_outline_white_24dp);
            item.setIconTintList(getActivity(), R.color.colorIcon);
            item.setChevron(View.VISIBLE);

            String hint = String.format("版本 %1$s", PackageUtils.getVersionName(getActivity()));
            item.setHint(hint);

            item.setConsumer((obj) -> AboutActivity.start(this));

            list.add(item);
        }

        return list;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {
        adapter.bind(ExplainSettingItem.class,
                new BridgeBuilder(ExplainSettingViewHolder.class, ExplainSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(ProfileSettingItem.class,
                new BridgeBuilder(ProfileSettingViewHolder.class, ProfileSettingViewHolder.LAYOUT_RESOURCE_ID));

        adapter.bind(BaseSettingItem.class,
                new BridgeBuilder(BaseSettingViewHolder.class, BaseSettingViewHolder.LAYOUT_RES_ID));
    }

    void onProfileChanged() {
        ProfileEntity entity = PreferenceEntity.obtain().getProfile();

        ProfileSettingItem item = getItem(ID_PROFILE);
        item.setName(entity.getName());
        item.setHint(entity.getSignature());
        item.setIconUri(entity.getPortraitUri());

        this.notifyItemChanged(item);
    }

    /**
     *
     */
    private static class RequestProfileDelegate extends BaseRequestDelegate {

        Consumer<Object> consumer;

        public RequestProfileDelegate(Fragment f, Consumer<Object> consumer) {
            super(f);

            this.consumer = consumer;
        }

        @Override
        public boolean request() {
            ProfileActivity.startForResult(parent, getRequestCode());

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            consumer.accept(data);
        }
    }

}
