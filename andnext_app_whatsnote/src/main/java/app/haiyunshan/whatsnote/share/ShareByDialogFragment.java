package app.haiyunshan.whatsnote.share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.share.database.ShareTable;
import club.andnext.dialog.BaseDialogFragment;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.utils.GsonUtils;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShareByDialogFragment extends BaseDialogFragment {

    RecyclerView menuRecyclerView;

    RecyclerView intentRecyclerView;

    TextView cancelBtn;

    MenuItem.OnMenuItemClickListener onMenuItemClickListener;
    OnResolveItemClickListener onResolveItemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_share_by, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.menuRecyclerView = view.findViewById(R.id.menu_recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            menuRecyclerView.setLayoutManager(layout);

            menuRecyclerView.setVisibility(View.GONE);
        }

        {
            this.intentRecyclerView = view.findViewById(R.id.intent_recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            intentRecyclerView.setLayoutManager(layout);

            intentRecyclerView.setVisibility(View.GONE);
        }

        {
            this.cancelBtn = view.findViewById(R.id.tv_cancel);
            cancelBtn.setOnClickListener(this::onCancelClick);
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = this.getArguments();

        if (args != null) {
            this.buildShare(args);
        }

    }

    void buildShare(Bundle args) {

        {
            MenuProvider provider = null;

            int menuRes = args.getInt("menuRes", 0);
            if (menuRes > 0) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuRecyclerView);
                popupMenu.inflate(menuRes);

                if (popupMenu.getMenu().size() > 0) {
                    provider = new MenuProvider(getActivity(), popupMenu);
                }
            }

            if (provider != null) {

                BridgeAdapter menuAdapter = new BridgeAdapter(getActivity(), provider);

                menuAdapter.bind(MenuEntity.class,
                        new BridgeBuilder(MenuViewHolder.class, MenuViewHolder.LAYOUT_RES_ID, this));

                menuRecyclerView.setAdapter(menuAdapter);
                this.menuRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        ResolveProvider provider = null;
        List<String> sortList = null;

        Intent[] intents = (Intent[]) (args.getParcelableArray("intents"));
        if (intents != null && intents.length > 0) {
            provider = new ResolveProvider(getActivity(), intents);
            if (provider.size() == 0) {
                provider = null;
            }
        }

        if (provider != null) {
            Comparator<ResolveEntity> c = Comparator.comparing(ResolveEntity::getTitle, Collator.getInstance()::compare);

            String[] sorts = args.getStringArray("sorts");
            if (sorts == null || sorts.length == 0) {
                ShareTable table = GsonUtils.fromJson(getActivity(), "share/share_ds.json", ShareTable.class);
                if (table != null) {
                    sortList = table.getList().stream()
                            .map(e -> e.getId())
                            .collect(Collectors.toList());
                }
            } else {
                sortList = Arrays.asList(sorts);
            }

            if (sortList != null && sortList.size() > 0) {
                c = new ResolveComparator(sortList).thenComparing(c);
            }

            provider.sort(c);

        }

        if (provider == null) {
            return;
        }

        if ((menuRecyclerView.getVisibility() == View.VISIBLE) || (sortList == null || sortList.isEmpty())) {
            BridgeAdapter intentAdapter = new BridgeAdapter(getActivity(), provider);

            intentAdapter.bind(ResolveEntity.class,
                    new BridgeBuilder(ResolveViewHolder.class, ResolveViewHolder.LAYOUT_RES_ID, this));

            intentRecyclerView.setAdapter(intentAdapter);
            this.intentRecyclerView.setVisibility(View.VISIBLE);

            return;
        }

        ArrayList<ResolveEntity> list = new ArrayList<>(sortList.size());
        for (String id : sortList) {
            int pos = provider.indexOf(id);
            if (pos >= 0) {
                ResolveEntity e = provider.get(pos);
                list.add(e);

                provider.remove(e);
            }
        }

        ResolveProvider favProvider = null;
        if (!list.isEmpty()) {
            favProvider = new ResolveProvider(list);
        }

        if (favProvider != null) {
            BridgeAdapter menuAdapter = new BridgeAdapter(getActivity(), favProvider);

            menuAdapter.bind(ResolveEntity.class,
                    new BridgeBuilder(ResolveViewHolder.class, ResolveViewHolder.LAYOUT_RES_ID, this));

            menuRecyclerView.setAdapter(menuAdapter);
            this.menuRecyclerView.setVisibility(View.VISIBLE);
        }

        if (provider != null && provider.size() > 0) {
            BridgeAdapter intentAdapter = new BridgeAdapter(getActivity(), provider);

            intentAdapter.bind(ResolveEntity.class,
                    new BridgeBuilder(ResolveViewHolder.class, ResolveViewHolder.LAYOUT_RES_ID, this));

            intentRecyclerView.setAdapter(intentAdapter);
            this.intentRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    void onCancelClick(View view) {
        this.dismiss();
    }

    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        this.onMenuItemClickListener = listener;
    }

    public void setOnResolveItemClickListener(OnResolveItemClickListener listener) {
        this.onResolveItemClickListener = listener;
    }

    public void setMenuResource(int menuRes) {

        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putInt("menuRes", menuRes);

        this.setArguments(args);
    }

    public void setIntent(Intent... intents) {
        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putParcelableArray("intents", intents);

        this.setArguments(args);
    }

    public void setSort(String... sorts) {
        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putStringArray("sorts", sorts);

        this.setArguments(args);
    }

    /**
     *
     */
    private class MenuViewHolder extends BridgeViewHolder<MenuEntity> {

        static final int LAYOUT_RES_ID = R.layout.layout_action_list_item;

        ImageView iconView;
        TextView nameView;

        @Keep
        public MenuViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.iconView = view.findViewById(R.id.iv_icon);
            iconView.setClipToOutline(true);

            this.nameView = view.findViewById(R.id.tv_name);
        }

        @Override
        public void onBind(MenuEntity item, int position) {
            iconView.setImageDrawable(item.getIcon());
            nameView.setText(item.getTitle());
        }

        void onItemClick(View view) {
            dismiss();

            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(getItem().item);
            }
        }
    }

    /**
     *
     */
    public interface OnResolveItemClickListener {
        void onResolveItemClick(ResolveInfo resolveInfo);
    }

    /**
     *
     */
    private static class MenuProvider implements ClazzAdapterProvider<MenuEntity> {

        List<MenuEntity> list;

        MenuProvider(Activity context, PopupMenu menu) {
            if (menu != null) {
                this.list = IntStream.range(0, menu.getMenu().size())
                        .mapToObj(i -> menu.getMenu().getItem(i))
                        .filter(item -> item.isVisible())
                        .map(item -> new MenuEntity(item))
                        .collect(Collectors.toList());
            } else {
                list = Collections.emptyList();
            }
        }

        @Override
        public MenuEntity get(int position) {
            return list.get(position);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    /**
     *
     */
    private static class MenuEntity {

        MenuItem item;

        MenuEntity(MenuItem item) {
            this.item = item;
        }

        public Drawable getIcon() { return item.getIcon(); }

        public CharSequence getTitle() {
            return item.getTitle();
        }

    }

    /**
     *
     */
    private class ResolveViewHolder extends BridgeViewHolder<ResolveEntity> {

        static final int LAYOUT_RES_ID = R.layout.layout_action_list_item;

        ImageView iconView;
        TextView nameView;

        @Keep
        public ResolveViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.iconView = view.findViewById(R.id.iv_icon);
            iconView.setClipToOutline(true);

            this.nameView = view.findViewById(R.id.tv_name);
        }

        @Override
        public void onBind(ResolveEntity item, int position) {
            iconView.setImageDrawable(item.getIcon());
            nameView.setText(item.getTitle());
        }

        void onItemClick(View view) {
           dismiss();

           if (onResolveItemClickListener != null) {
               onResolveItemClickListener.onResolveItemClick(getItem().item);
           }
        }
    }

    /**
     *
     */
    private static class ResolveProvider implements ClazzAdapterProvider<ResolveEntity> {

        List<ResolveEntity> list;

        ResolveProvider(Activity context, Intent[] intents) {

            {
                this.list = new ArrayList<>();
            }

            {
                PackageManager pm = context.getPackageManager();

                for (int i = 0; i < intents.length; i++) {

                    Intent intent = intents[i];
                    List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
                    for (ResolveInfo info : list) {
                        this.add(context, info);
                    }
                }
            }
        }

        ResolveProvider(List<ResolveEntity> list) {
            this.list = list;
        }

        void add(Activity context, ResolveInfo resolveInfo) {
            if (indexOf(resolveInfo.activityInfo.name) >= 0) {
                return;
            }

            list.add(new ResolveEntity(context, resolveInfo));
        }

        boolean remove(ResolveEntity entity) {
            return list.remove(entity);
        }

        int indexOf(String id) {
            ResolveEntity entity = list.stream()
                    .filter(e -> e.getId().equals(id))
                    .findAny()
                    .orElse(null);

            if (entity == null) {
                return -1;
            }

            return list.indexOf(entity);
        }

        void sort(Comparator<ResolveEntity> comparator) {
            list.sort(comparator);
        }

        @Override
        public ResolveEntity get(int position) {
            return list.get(position);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    /**
     *
     */
    private static class ResolveEntity {

        ResolveInfo item;

        Activity context;

        ResolveEntity(Activity context, ResolveInfo item) {
            this.context = context;

            this.item = item;
        }

        String getId() {
            return item.activityInfo.name;
        }

        Drawable getIcon() {
            return item.loadIcon(context.getPackageManager());
        }

        CharSequence getTitle() {
            return item.loadLabel(context.getPackageManager());
        }

    }

    /**
     *
     */
    private static class ResolveComparator implements Comparator<ResolveEntity> {

        List<String> array;

        ResolveComparator(List<String> array) {
            this.array = array;
        }

        int indexOf(String id) {

            int index = -1;
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).equalsIgnoreCase(id)) {
                    index = i;
                    break;
                }
            }

            return index;
        }

        int getPriority(String id) {
            int index = indexOf(id);
            if (index < 0) {
                return index;
            }

            return array.size() - index;
        }

        @Override
        public int compare(ResolveEntity o1, ResolveEntity o2) {
            int p1 = getPriority(o1.getId());
            int p2 = getPriority(o2.getId());

            return (p2 - p1);
        }
    }
}
