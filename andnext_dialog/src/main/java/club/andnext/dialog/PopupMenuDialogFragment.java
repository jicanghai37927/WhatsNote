package club.andnext.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PopupMenuDialogFragment extends BaseDialogFragment {

    protected RecyclerView recyclerView;
    protected BridgeAdapter adapter;

    protected TextView cancelBtn;

    boolean cancelVisible;
    int buttonAppearance;

    protected MenuItem.OnMenuItemClickListener onMenuItemClickListener;

    public PopupMenuDialogFragment() {

    }

    public void setMenuResource(int menuRes, int... exclude) {

        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putInt("menuRes", menuRes);
        if (exclude != null && exclude.length > 0) {
            args.putIntArray("exclude", exclude);
        }

        this.setArguments(args);
    }

    public void setButtonAppearance(@StyleRes int resId) {

        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putInt("buttonAppearance", resId);

        this.setArguments(args);
    }

    public void setCancelVisible(boolean visible) {

        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putBoolean("cancelVisible", visible);

        this.setArguments(args);
    }

    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        this.onMenuItemClickListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.anc_dialog_fragment_popup_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.cancelBtn = view.findViewById(R.id.anc_tv_cancel);
            cancelBtn.setOnClickListener(this::onCancelClick);
        }

        {
            this.recyclerView = view.findViewById(R.id.anc_recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            MarginDividerDecoration dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setMargin(0);
            dividerDecoration.setLastMargin(false);
            dividerDecoration.setDrawTop(false);
            dividerDecoration.setDrawBottom(false);

            recyclerView.addItemDecoration(dividerDecoration);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();

        PopupMenu popupMenu;

        {
            int menuRes = args.getInt("menuRes");
            popupMenu = new PopupMenu(getActivity(), recyclerView);
            popupMenu.inflate(menuRes);

            int[] exclude = args.getIntArray("exclude");
            if (exclude != null) {
                for (int id : exclude) {
                    MenuItem item = popupMenu.getMenu().findItem(id);
                    if (item != null) {
                        item.setVisible(false);
                    }
                }
            }
        }

        {
            this.buttonAppearance = args.getInt("buttonAppearance", 0);
        }

        {
            this.cancelVisible = args.getBoolean("cancelVisible", true);
            ((View)(cancelBtn.getParent())).setVisibility(cancelVisible? View.VISIBLE: View.GONE);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new MenuProvider(popupMenu));

            adapter.bind(MenuEntity.class,
                    new BridgeBuilder(MenuViewHolder.class, MenuViewHolder.LAYOUT_RES_ID, this));

        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    void onCancelClick(View view) {
        this.dismiss();

        onCancel(this.getDialog());
    }

    void onMenuItemClick(MenuItem item) {
        this.dismiss();

        if (onMenuItemClickListener != null) {
            onMenuItemClickListener.onMenuItemClick(item);
        }
    }

    /**
     *
     */
    private static class MenuProvider implements ClazzAdapterProvider<MenuEntity> {

        List<MenuEntity> list;

        MenuProvider(PopupMenu menu) {
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

        public CharSequence getTitle() {
            return item.getTitle();
        }
    }

    /**
     *
     */
    private static class MenuViewHolder extends BridgeViewHolder<MenuEntity> {

        static final int LAYOUT_RES_ID = R.layout.anc_layout_menu_list_item;

        TextView nameView;

        PopupMenuDialogFragment parent;

        @Keep
        public MenuViewHolder(PopupMenuDialogFragment f, View itemView) {
            super(itemView);

            parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.nameView = view.findViewById(R.id.anc_tv_name);
            if (parent.buttonAppearance > 0) {
                nameView.setTextAppearance(parent.buttonAppearance);
            }
        }

        @Override
        public void onBind(MenuEntity item, int position) {
            nameView.setText(item.getTitle());
        }

        void onItemClick(View view) {
            parent.onMenuItemClick(getItem().item);
        }
    }
}
