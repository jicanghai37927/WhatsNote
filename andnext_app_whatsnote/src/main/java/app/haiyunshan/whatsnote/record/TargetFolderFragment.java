package app.haiyunshan.whatsnote.record;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.SortEntity;
import app.haiyunshan.whatsnote.widget.RecordIconView;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.decoration.PlaceholderDecoration;
import club.andnext.recyclerview.tree.TreeList;
import club.andnext.recyclerview.tree.TreeListAdapterCallback;
import club.andnext.utils.AlertDialogUtils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 */
public class TargetFolderFragment extends Fragment {

    TextView descView;
    TitleBar titleBar;
    RecordIconView iconView;
    TextView nameView;

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    TreeList treeList;
    MarginDividerDecoration dividerDecoration;

    RecordEntity data;
    RecordEntity activated;

    HashMap<String, RecordEntity> recordMap;

    SortEntity sortEntity;

    int levelPadding;

    public TargetFolderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_target_folder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setDrawOver(false);
            dividerDecoration.setDrawTop(false);
            dividerDecoration.setLastMargin(true);
            dividerDecoration.setMargin(0);
            dividerDecoration.setAlphaEnable(false);
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
//            FadeInDownAnimator animator = new FadeInDownAnimator();
//            recyclerView.setItemAnimator(animator);
        }

        {
            PlaceholderDecoration decoration = new PlaceholderDecoration(getActivity());
            decoration.setMargin(getResources().getDimensionPixelSize(R.dimen.entrance_item_padding));
            recyclerView.addItemDecoration(decoration, 0);
        }

        {
            this.descView = view.findViewById(R.id.tv_desc);

            this.titleBar = view.findViewById(R.id.title_bar);
            titleBar.setNegativeButton("取消", this::onNegativeClick);
            titleBar.setPositiveButton("移动", this::onPositiveClick);
            titleBar.setPositiveEnable(false);

            this.iconView = view.findViewById(R.id.iv_icon);

            this.nameView = view.findViewById(R.id.tv_name);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = (args.getString("id", ""));
            this.data = RecordEntity.list(id);

            this.sortEntity = SortEntity.create(SortEntity.ID_NAME);

            this.levelPadding = getResources().getDimensionPixelSize(R.dimen.levelPadding);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new FolderProvider());

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, this));
        }

        {
            this.treeList = new TreeList(new TreeListAdapterCallback(adapter));
            treeList.setKeepExpand(true);
            RecordEntity root = getEntity(RecordEntity.ROOT_NOTE);
            root.setName("我的笔记");

            treeList.add(null, root);
        }

        {
            recyclerView.setAdapter(adapter);
        }

        if (!data.isEmpty()) {
            RecordEntity entity = data.get(0);
            iconView.setIcon(entity);
            nameView.setText(entity.getName());
        }

        {
            updateHint(activated);
        }
    }

    void onNegativeClick(View view) {
        getActivity().onBackPressed();
    }

    void onPositiveClick(View view) {
        if (activated == null) {
            return;
        }

        if (data.isEmpty()) {

            getActivity().onBackPressed();

            return;
        }

        RecordEntity entity = data.get(0);

        if (activated.isDescendantOf(entity.getId())) {

            String title = "不能完成此操作。";
            String msg = "未能存储该笔记。";
            if (data.size() == 1) {
                msg = String.format("未能存储笔记“%1$s”。", data.get(0).getName());
            }

            AlertDialogUtils.showMessage(getActivity(), title, msg);

            return;
        }

        {

            boolean result = entity.setParent(activated);

            if (result) {
                getActivity().onBackPressed();
            }
        }

        {
            entity.save();
        }

    }

    RecordEntity getEntity(String id) {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }

        RecordEntity entity = recordMap.get(id);
        if (entity != null) {
            return entity;
        }

        entity = RecordEntity.listFolder(id);
        if (!entity.isEmpty()) {
            entity.getList().sort(sortEntity.getComparator());
        }

        recordMap.put(id, entity);
        return entity;
    }

    boolean isActivated(RecordEntity entity) {
        return (activated == entity);
    }

    void setActivated(RecordEntity entity) {
        if (isActivated(entity)) {
            return;
        }

        if (!isSelectable(entity)) {
            entity = null;
        }

        int oldPosition = -1;
        if (activated != null) {
            oldPosition = treeList.indexOf(activated);
        }

        int position = -1;
        this.activated = entity;
        if (activated != null) {
            position = treeList.indexOf(activated);
        }

        if (oldPosition >= 0) {
            adapter.notifyItemChanged(oldPosition);
        }

        if (position >= 0) {
            adapter.notifyItemChanged(position);
        }

        titleBar.setPositiveEnable(activated != null);
        updateHint(activated);
    }

    boolean isSelectable(RecordEntity entity) {
        return entity != null;
    }

    void updateHint(RecordEntity item) {
        String text = "选取新的位置以移动此项目。";
        if (data.size() > 1) {
            text = "选取新的位置以移动这些项目。";
        }

        if (item != null) {
            text = String.format("项目将移动到“%1$s”。", item.getName());
        }

        descView.setText(text);
    }

    /**
     *
     */
    private class FolderProvider implements ClazzAdapterProvider<RecordEntity> {

        @Override
        public RecordEntity get(int position) {
            return (RecordEntity)treeList.get(position);
        }

        @Override
        public int size() {
            return treeList.size();
        }
    }

    /**
     *
     */
    private static class FolderViewHolder extends BridgeViewHolder<RecordEntity> implements MarginDividerDecoration.Adapter {

        static final int LAYOUT_RES_ID = R.layout.layout_folder_tree_list_item;

        View contentLayout;

        View levelView;

        ImageView iconView;
        TextView nameView;

        ImageView chevronView;

        TargetFolderFragment parent;

        @Keep
        public FolderViewHolder(TargetFolderFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {

            this.contentLayout = view.findViewById(R.id.content_layout);
            contentLayout.setOnClickListener(this::onItemClick);

            this.levelView = view.findViewById(R.id.tv_level);
            this.iconView = view.findViewById(R.id.iv_icon);
            this.nameView = view.findViewById(R.id.tv_name);
            this.chevronView = view.findViewById(R.id.iv_right);
        }

        @Override
        public void onBind(RecordEntity item, int position) {

            {
                contentLayout.setActivated(parent.isActivated(item));
            }

            {
                int level = parent.treeList.getLevel(item);
                level = (level < 0)? 0: level;
                int padding = level * parent.levelPadding;

                int left = padding;
                int top = 0;
                int right = 0;
                int bottom = 0;

                levelView.setPadding(left, top, right, bottom);
            }

            {
                if (item.getId().equals(RecordEntity.ROOT_NOTE)) {
                    iconView.setImageResource(R.drawable.ic_phone);
                    iconView.setImageTintMode(null);
                } else {
                    iconView.setImageResource(R.drawable.ic_folder_white_24dp);
                    iconView.setImageTintMode(PorterDuff.Mode.SRC_IN);
                }
            }

            {
                nameView.setText(item.getName());
            }

            {
                chevronView.setRotation(parent.treeList.isExpand(item) ? 90 : 0);
                chevronView.setVisibility((item.size() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        }

        void onItemClick(View view) {

            RecordEntity entity = getItem();

            parent.setActivated(entity);

            if (entity.size() == 0) {
                return;
            }

            boolean isExpand = (parent.treeList.isExpand(entity));
            if (!isExpand) {
                for (int i = 0, size = entity.size(); i < size; i++) {
                    RecordEntity child = entity.get(i);

                    parent.treeList.add(entity, parent.getEntity(child.getId()));
                }
            }

            parent.treeList.setExpand(entity, !isExpand);

            chevronView.animate().rotation(parent.treeList.isExpand(entity)? 90: 0);
        }

        @Override
        public float getTranslation(MarginDividerDecoration decoration) {
            return nameView.getLeft();
        }
    }
}
