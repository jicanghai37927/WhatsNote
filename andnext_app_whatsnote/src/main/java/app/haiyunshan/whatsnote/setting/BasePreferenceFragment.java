package app.haiyunshan.whatsnote.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.BaseSettingItem;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.decoration.SectionDividerDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BasePreferenceFragment extends Fragment {

    protected SearchTitleBar titleBar;

    protected RecyclerView recyclerView;
    BridgeAdapter adapter;
    SettingProvider provider;

    protected SectionDividerDecoration dividerDecoration;

    public BasePreferenceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preference, container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);

            Toolbar toolbar = titleBar.getToolbar();
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(this::onNavigationClick);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);

            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            this.dividerDecoration = new SectionDividerDecoration(getActivity());
            recyclerView.addItemDecoration(dividerDecoration);
        }
    }

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            List<? extends BaseSettingItem> list = createList();
            list = (list == null)? Collections.emptyList(): list;
            this.provider = new SettingProvider(list);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), provider);
            this.buildAdapter(adapter);
        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    protected void onNavigationClick(View view) {
        getActivity().onBackPressed();
    }

    protected void setTitle(CharSequence text) {
        titleBar.setTitle(text);
    }

    protected void replaceAll(List<BaseSettingItem> list) {
        provider.setList(list);
        adapter.notifyItemRangeInserted(0, list.size());
    }

    protected <T> T getItem(String id) {
        return provider.getItem(id);
    }

    protected void remove(String id) {
        Object obj = getItem(id);
        int index = indexOf(obj);
        if (index < 0) {
            return;
        }

        provider.remove(index);
        adapter.notifyItemRemoved(index);
    }

    protected int indexOf(Object object) {
        return provider.indexOf(object);
    }

    public void notifyItemChanged(Object object) {
        int index = indexOf(object);

        notifyItemChanged(index);
    }

    public void notifyItemChanged(int index) {
        if (index >= 0) {
            adapter.notifyItemChanged(index);
        }
    }

    protected abstract List<? extends BaseSettingItem> createList();

    protected abstract void buildAdapter(BridgeAdapter adapter);

    /**
     *
     */
    class SettingProvider implements BridgeAdapterProvider<BaseSettingItem> {

        List<BaseSettingItem> list;

        public SettingProvider(List<? extends BaseSettingItem> list) {
            this.list = new ArrayList<>(list);
        }

        void setList(List<BaseSettingItem> list) {
            this.list = list;
        }

        <T> T getItem(String id) {
            BaseSettingItem item = list.stream()
                    .filter(e -> e.getId().equals(id))
                    .findAny()
                    .orElse(null);

            return (T)item;
        }

        int indexOf(Object object) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (list.get(i) == object) {
                    return i;
                }
            }

            return -1;
        }

        BaseSettingItem remove(int index) {
            return list.remove(index);
        }

        @Override
        public BaseSettingItem get(int position) {
            return list.get(position);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

}
