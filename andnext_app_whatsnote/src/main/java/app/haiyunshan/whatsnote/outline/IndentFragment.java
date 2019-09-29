package app.haiyunshan.whatsnote.outline;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.base.OnFragmentInteractionListener;
import app.haiyunshan.whatsnote.setting.BaseOptionFragment;
import app.haiyunshan.whatsnote.setting.item.BaseSettingItem;
import app.haiyunshan.whatsnote.setting.item.NumericalSettingItem;
import app.haiyunshan.whatsnote.setting.viewholder.BaseSettingViewHolder;
import app.haiyunshan.whatsnote.setting.viewholder.NumericalSettingViewHolder;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndentFragment extends BaseOptionFragment {

    public static final String TYPE_CHINESE = "chinese";
    public static final String TYPE_ENGLISH = "english";

    private static int sRecentCount = 2;
    private static String sRecentType = TYPE_CHINESE;

    int count;
    String type;

    public IndentFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.count = sRecentCount;
        this.type = sRecentType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            titleBar.setEdit(true);

            TitleBar bar = titleBar.getEditBar();
            bar.setTitle("段落缩进");
            bar.setNegativeButton("取消", this::onCancelClick);
            bar.setPositiveButton("完成", this::onDoneClick);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        {
            sRecentCount = count;
            sRecentType = type;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setArguments(String type) {
        Bundle args = this.getArguments();
        args = args == null? new Bundle(): new Bundle(args);

        args.putString("type", type);

        this.setArguments(args);

        {
            this.type = type;
        }
    }

    @Override
    protected List<BaseSettingItem> createList() {
        ArrayList<BaseSettingItem> list = new ArrayList<>();

        {
            int value = this.count;
            int min = 0;
            int max = 12;
            int step = 1;

            NumericalSettingItem item = new NumericalSettingItem(value, min, max, step, null);
            item.setConsumer(length -> this.count = length);
            item.setName("字符个数");
            item.setChevron(View.GONE);

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem()
                    .setConsumer((object -> requestType()))
                    .setName("字符类型")
                    .setHint(this.type.equalsIgnoreCase(TYPE_CHINESE)? "中文": "英文")
                    .setChevron(View.VISIBLE);
            list.add(item);
        }

        return list;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {

        adapter.bind(NumericalSettingItem.class,
                new BridgeBuilder(NumericalSettingViewHolder.class, NumericalSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(BaseSettingItem.class,
                new BridgeBuilder(BaseSettingViewHolder.class, BaseSettingViewHolder.LAYOUT_RES_ID));

    }

    void onCancelClick(View view) {
        OnFragmentInteractionListener listener = getInteraction();
        if (listener != null) {
            Uri uri = Uri.parse("indent://indent?action=cancel");
            listener.onInteraction(this, uri);
        }
    }

    void onDoneClick(View view) {
        OnFragmentInteractionListener listener = getInteraction();
        if (listener != null) {
            Uri uri = Uri.parse(String.format("indent://indent?action=done&count=%1$d&type=%2$s", this.count, this.type));
            listener.onInteraction(this, uri);
        }
    }

    void requestType() {
        OnFragmentInteractionListener listener = getInteraction();
        if (listener != null) {
            Uri uri = Uri.parse("indent://indent?action=type&type=" + this.type);
            listener.onInteraction(this, uri);
        }
    }


}
