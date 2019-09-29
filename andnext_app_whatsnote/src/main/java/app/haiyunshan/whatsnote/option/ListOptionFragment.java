package app.haiyunshan.whatsnote.option;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.base.OnFragmentInteractionListener;
import app.haiyunshan.whatsnote.setting.BaseOptionFragment;
import app.haiyunshan.whatsnote.setting.item.BaseSettingItem;
import app.haiyunshan.whatsnote.setting.item.CheckSettingItem;
import app.haiyunshan.whatsnote.setting.viewholder.CheckSettingViewHolder;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListOptionFragment extends BaseOptionFragment {

    List<CharSequence> list;
    int selectedIndex = -1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        if (args != null) {
            this.list = args.getCharSequenceArrayList("list");
            this.selectedIndex = args.getInt("selectedIndex", -1);
        }

        if (list == null) {
            list = Collections.emptyList();
        }

        {
            CharSequence title = args.getCharSequence("title", "");
            titleBar.setTitle(title);
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void setArguments(@NonNull String id, @NonNull CharSequence title, @NonNull ArrayList<CharSequence> list, int selectedIndex) {

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putCharSequence("title", title);
        args.putCharSequenceArrayList("list", list);
        args.putInt("selectedIndex", selectedIndex);

        super.setArguments(args);
    }

    @Override
    protected void onNavigationClick(View view) {
        OnFragmentInteractionListener listener = getInteraction();
        if (listener != null) {
            Uri uri = Uri.parse(String.format("option://option?id=%1$s&index=%2$d",
                    getArguments().getString("id"),
                    selectedIndex));

            listener.onInteraction(this, uri);
        }
    }

    @Override
    protected List<? extends BaseSettingItem> createList() {
        ArrayList<CheckSettingItem> out = new ArrayList<>(list.size());

        int index = 0;
        for (CharSequence text : list) {
            CheckSettingItem item = new CheckSettingItem(pos -> (pos == this.selectedIndex));
            item.setName(text);
            item.setUserObject(index);
            item.setConsumer(this::onSelectChanged);
            item.setId(String.valueOf(index));
            item.setChevron(View.GONE);

            out.add(item);

            ++index;
        }

        return out;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {
        adapter.bind(CheckSettingItem.class,
                new BridgeBuilder(CheckSettingViewHolder.class, CheckSettingViewHolder.LAYOUT_RES_ID));
    }

    void onSelectChanged(int index) {
        if (selectedIndex == index) {
            return;
        }

        int pre = selectedIndex;
        selectedIndex = index;

        if (pre >= 0) {
            notifyItemChanged(pre);
        }

        if (index >= 0) {
            notifyItemChanged(index);
        }

    }

}
