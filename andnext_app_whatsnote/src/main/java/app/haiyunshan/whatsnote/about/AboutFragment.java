package app.haiyunshan.whatsnote.about;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.share.ShareIntentAction;
import app.haiyunshan.whatsnote.poster.PosterUtils;
import app.haiyunshan.whatsnote.setting.BasePreferenceFragment;
import app.haiyunshan.whatsnote.setting.item.*;
import app.haiyunshan.whatsnote.setting.viewholder.*;
import app.haiyunshan.whatsnote.share.ShareByDialogFragment;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.utils.AssetUtils;
import club.andnext.utils.PackageUtils;
import club.andnext.utils.WebUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends BasePreferenceFragment {

    public AboutFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setTitle("关于");

        dividerDecoration.setDrawTop(false);
    }

    void requestRank() {

        if (getMarketCount() == 1) {

            PackageUtils.showMarket(getActivity());
            return;
        }

        Intent[] intents = new Intent[] {
                getMarketIntent()
        };

        ShareByDialogFragment.OnResolveItemClickListener listener = (resolveInfo -> {
            Intent intent = getMarketIntent();

            String pkg = resolveInfo.activityInfo.packageName;
            String cls = resolveInfo.activityInfo.name;
            ComponentName cn = new ComponentName(pkg, cls);
            intent.setComponent(cn);

            // start
            try {
                startActivity(intent);
            } catch (Exception e) {

            }
        });

        ShareByDialogFragment dialogFragment = new ShareByDialogFragment();
        dialogFragment.setTheme(R.style.DialogDimTheme);
        dialogFragment.setIntent(intents);
        dialogFragment.setOnResolveItemClickListener(listener);

        dialogFragment.showNow(getChildFragmentManager(), "rank");

    }

    void requestShare() {

        Intent[] intents = new Intent[] {
                new Intent().setAction(Intent.ACTION_SEND).setType("text/plain")
        };

        ShareByDialogFragment.OnResolveItemClickListener listener = (resolveInfo -> {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setAction(Intent.ACTION_SEND).setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getShareText());

            String pkg = resolveInfo.activityInfo.packageName;
            String cls = resolveInfo.activityInfo.name;
            ComponentName cn = new ComponentName(pkg, cls);
            intent.setComponent(cn);

            // start
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ShareByDialogFragment dialogFragment = new ShareByDialogFragment();

        dialogFragment.setTheme(R.style.DialogDimTheme);
        dialogFragment.setIntent(intents);
        dialogFragment.setOnResolveItemClickListener(listener);
        dialogFragment.showNow(getChildFragmentManager(), "share");
    }

    long getMarketCount() {
        Intent intent = getMarketIntent();
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        return (list == null)? 0: list.size();
    }

    Intent getMarketIntent() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    String getShareText() {
        String text = AssetUtils.getString(getActivity(), "about/share.txt");
        if (TextUtils.isEmpty(text)) {
            text = "下载安装「神马笔记」，我在手机上都用它来做笔记，非常好用的APP。下载地址：http://andnext.club/";
        }

        return text;
    }

    @Override
    protected List<? extends BaseSettingItem> createList() {
        ArrayList<BaseSettingItem> list = new ArrayList<>();

        {
            IconSettingItem item = new IconSettingItem();

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");

            list.add(item);
        }

        {
            CaptionSettingItem item = new CaptionSettingItem();
            CharSequence text = "神马笔记 WhatsNote";
            item.setName(text);
            item.setMinLines(1);
            item.setPaddingTop(0);
            item.setPaddingBottom(0);
            item.setAppearance(R.style.TextAppearance_AppCompat_Title);

            list.add(item);
        }

        {
            CaptionSettingItem item = new CaptionSettingItem();
            CharSequence text = "Version " + PackageUtils.getVersionName(getActivity());
            item.setHint(text);
            item.setMinLines(1);
            item.setPaddingTop(0);
            item.setPaddingBottom(0);

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        if (getMarketCount() > 0) {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("评分");
            item.setChevron(View.INVISIBLE);
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);
            item.setConsumer(object -> requestRank());

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("分享");
            item.setChevron(View.INVISIBLE);
            item.setConsumer((object -> requestShare()));

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("联系我们");
            item.setChevron(View.INVISIBLE);
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);
            item.setConsumer((obj) -> {
                String url = "http://andnext.club/whatsnote/contact.html";
                url = WebUtils.withTimestamp(url);

                PackageUtils.startBrowser(getActivity(), Uri.parse(url));
            });

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("功能介绍");
            item.setChevron(View.INVISIBLE);
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);
            item.setConsumer((obj) -> {
                String url = "http://andnext.club/whatsnote/intro.html";
                url = WebUtils.withTimestamp(url);

                PackageUtils.startBrowser(getActivity(), Uri.parse(url));
            });

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("开发者博客");
            item.setChevron(View.INVISIBLE);
            item.setConsumer((obj) -> {
                String url = "http://andnext.club/";
                url = WebUtils.withTimestamp(url);

                PackageUtils.startBrowser(getActivity(), Uri.parse(url));
            });

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("远望");
            item.setChevron(View.INVISIBLE);
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);
            item.setConsumer((obj) -> {
                String url = "http://andnext.club/whatsnote/vision.html";
                url = WebUtils.withTimestamp(url);

                PackageUtils.startBrowser(getActivity(), Uri.parse(url));
            });

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("致谢");
            item.setChevron(View.INVISIBLE);
            item.setConsumer((obj) -> {
                String url = "http://andnext.club/whatsnote/acknowledgements.html";
                url = WebUtils.withTimestamp(url);

                PackageUtils.startBrowser(getActivity(), Uri.parse(url));
            });

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");

            list.add(item);
        }

        {
            CaptionSettingItem item = new CaptionSettingItem();
            CharSequence text = "厦门海云山网络科技有限公司 版权所有\nCopyright © 2018-2019 All Rights Reserved";
            item.setHint(text);
            item.setMinLines(1);
            item.setPaddingTop(0);
            item.setPaddingBottom(0);
            item.setAppearance(R.style.TextAppearance_AppCompat_Caption);

            list.add(item);
        }

        return list;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {
        adapter.bind(ExplainSettingItem.class,
                new BridgeBuilder(ExplainSettingViewHolder.class, ExplainSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(CaptionSettingItem.class,
                new BridgeBuilder(CaptionSettingViewHolder.class, CaptionSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(IconSettingItem.class,
                new BridgeBuilder(IconSettingViewHolder.class, IconSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(BaseSettingItem.class,
                new BridgeBuilder(BaseSettingViewHolder.class, BaseSettingViewHolder.LAYOUT_RES_ID));
    }

}
