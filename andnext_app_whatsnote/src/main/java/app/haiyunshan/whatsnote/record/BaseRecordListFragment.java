package app.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import app.haiyunshan.whatsnote.*;
import app.haiyunshan.whatsnote.article.delegate.ShareDelegate;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.base.OnRestartListener;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.record.entity.*;
import app.haiyunshan.whatsnote.record.viewholder.FolderViewHolder;
import app.haiyunshan.whatsnote.record.viewholder.NoteViewHolder;
import app.haiyunshan.whatsnote.record.viewholder.RecordViewHolder;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import club.andnext.dialog.PopupMenuDialogFragment;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeFilter;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.decoration.RemoveDecoration;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.utils.AlertDialogUtils;
import club.andnext.utils.FileUtils;
import club.andnext.utils.SoftInputUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseRecordListFragment extends Fragment implements OnRestartListener {

    SearchTitleBar titleBar;

    AppBarLayout appBarLayout;

    RecyclerView recyclerView;

    BridgeAdapter adapter;
    MarginDividerDecoration dividerDecoration;
    RemoveDecoration removeDecoration;

    SwipeActionHelper swipeActionHelper;

    RecordProvider recordProvider;
    CreateNoteHeader createNoteHeader;
    SortedList<RecordEntity> sortedList;

    SortEntity sortEntity;

    Optional<String> pendingId;

    Callback callback;

    RequestResultManager requestResultManager;

    public BaseRecordListFragment() {
        this.pendingId = Optional.ofNullable(null);

        this.requestResultManager = new RequestResultManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_base_record_list, container, false);

    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            if (titleBar != null) {
                Toolbar toolbar = titleBar.getToolbar();
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                toolbar.setNavigationOnClickListener(this::onNavigationClick);

                toolbar.inflateMenu(R.menu.menu_record_list);
                toolbar.setOnMenuItemClickListener(this.createMenuItemClickListener());

                titleBar.getSearchButton().setOnClickListener(this::onSearchClick);
            }

        }

        {
            this.appBarLayout = view.findViewById(R.id.app_bar_layout);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }


        {
            this.removeDecoration = new RemoveDecoration(getActivity());
            recyclerView.addItemDecoration(removeDecoration);
        }

        {
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setDrawTop(false);
            dividerDecoration.setDrawBottom(false);
            dividerDecoration.setMargin(getResources().getDimensionPixelSize(R.dimen.record_item_padding));
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
            this.swipeActionHelper = new SwipeActionHelper();
            swipeActionHelper.setOnSwipeActionListener(null);

            swipeActionHelper.attach(recyclerView);
        }

    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Callback callback = this.getCallback();

            this.createNoteHeader = new CreateNoteHeader(callback.isCreateEnable());
            this.sortEntity = callback.getSortEntity();

            if (titleBar != null) {
                MenuItem item = titleBar.getToolbar().getMenu().findItem(R.id.menu_add);
                if (item != null) {
                    item.setEnabled(callback.isCreateEnable());
                }
            }
        }

        {
            SortedListCallback callback = new SortedListCallback();
            this.sortedList = new SortedList<>(RecordEntity.class, callback);
        }

        {
            ViewHolderCallback viewHolderCallback = new ViewHolderCallback(this);

            this.recordProvider = new RecordProvider();
            this.adapter = new BridgeAdapter(getActivity(), recordProvider);

            adapter.bind(CreateNoteHeader.class,
                    new BridgeBuilder(CreateNoteViewHolder.class, CreateNoteViewHolder.LAYOUT_RES_ID, this)
                            .setParameterTypes(BaseRecordListFragment.class));

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(NoteViewHolder.class, NoteViewHolder.LAYOUT_RES_ID, viewHolderCallback)
                            .setParameterTypes(RecordViewHolder.Callback.class));

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, viewHolderCallback)
                            .setParameterTypes(RecordViewHolder.Callback.class));

            adapter.bind(RecordEntity.class, new BridgeFilter<RecordEntity>() {
                @Override
                public Class<? extends BridgeViewHolder> getHolder(RecordEntity obj) {
                    if (obj.isDirectory()) {
                        return FolderViewHolder.class;
                    }

                    return NoteViewHolder.class;
                }
            });
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @CallSuper
    public void onPause() {
        super.onPause();
    }

    @Override
    @CallSuper
    public void onRestart() {

    }

    final void onNavigationClick(View v) {
        getActivity().onBackPressed();
    }

    final void onSearchClick(View view) {
        this.clearSwipe();

        {
            SearchRecordActivity.start(this);
        }
    }

    abstract Callback createCallback();

    final Callback getCallback() {
        if (callback != null) {
            return callback;
        }

        callback = this.createCallback();
        if (callback == null) {
            callback = new DefaultRecordCallback();
        }

        return callback;
    }

    @CallSuper
    void setCreateEnable(boolean enable) {

        if (!(callback.isCreateEnable() ^ enable)) {
            return;
        }

        callback.createEnable = enable;

        // titleBar create button
        if (titleBar != null) {
            MenuItem item = titleBar.getToolbar().getMenu().findItem(R.id.menu_add);
            if (item != null) {
                item.setEnabled(callback.isCreateEnable());
            }
        }

        // list header
        if ((createNoteHeader.isChecked() ^ enable)) {
            createNoteHeader.setChecked(enable);

            if (enable) {
                adapter.notifyItemInserted(0);
            } else {
                adapter.notifyItemRemoved(0);
            }
        }
    }

    final SortEntity getSort() {
        return this.sortEntity;
    }

    @CallSuper
    void setSort(SortEntity entity) {

        if (sortEntity.getId().equals(entity.getId())) {
            sortEntity.toggle();
        } else {
            sortEntity = entity;
        }

        {
            RecordEntity[] array = IntStream.range(0, sortedList.size())
                    .mapToObj(i -> sortedList.get(i))
                    .toArray(RecordEntity[]::new);
            sortedList.replaceAll(array, true);
        }

        {
            recyclerView.scrollToPosition(0);
        }
    }

    final void replaceAll(List<RecordEntity> list) {
        if (list.isEmpty()) {
            sortedList.clear();
        } else {
            if (sortedList.size() == 0) {
                sortedList.addAll(list);
            } else {
                sortedList.replaceAll(list);
            }
        }

        pendingId.ifPresent((id) -> {

            this.scrollToEntity(id);

            pendingId = Optional.ofNullable(null);

        });

    }

    final int add(RecordEntity item) {
        return sortedList.add(item);
    }

    final boolean remove(RecordEntity item) {
        return sortedList.remove(item);
    }

    final void requestEdit(RecordEntity entity) {
        String parentId = entity.getParent();
        String id = entity.getId();

        requestEdit(parentId, id);
    }

    final void requestEdit(String parentId, String id) {

        RequestEditDelegate delegate = new RequestEditDelegate(this, parentId, id);
        requestResultManager.request(delegate);
    }

    final void requestCompose() {

        requestCompose(RecordEntity.STYLE_ARTICLE);


    }

    final void requestCompose(String style) {
        RecordEntity entity = getCallback().onCreateNote(style);
        if (entity != null) {
            requestCompose(entity);
        }
    }

    final void requestCompose(RecordEntity entity) {
        if (entity.isDirectory()) {
            return;
        }

        if (entity.isTrash()) {
            return;
        }

        RequestComposeDelegate delegate = new RequestComposeDelegate(this, entity);
        requestResultManager.request(delegate);
    }

    final void requestMove(RecordEntity entity) {
        if (entity.isTrash()) {
            return;
        }

        RequestMoveDelegate delegate = new RequestMoveDelegate(this, entity.getId());
        requestResultManager.request(delegate);
    }

    final void requestTrash(@Nullable RecordViewHolder viewHolder, RecordEntity entity) {
        if (entity.isTrash()) {
            return;
        }

        boolean result = entity.trash();
        if (result) {

            if (viewHolder != null) {
                viewHolder.hide();
                this.addRemove(viewHolder);
            }

            getCallback().onRemove(entity, false);
        }
    }

    final void requestDeleteNow(RecordViewHolder viewHolder, RecordEntity entity, boolean showConfirm) {
        if (!entity.isTrash()) {
            this.requestTrash(viewHolder, entity);
        } else {
            Context context = getActivity();
            CharSequence title = String.format("您确定要删除“%1$s”吗？", entity.getName());
            CharSequence msg = "将立即删除此项目。您不能撤销此操作。";
            CharSequence negativeButton = "取消";
            CharSequence positiveButton = "删除";
            DialogInterface.OnClickListener listener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    getCallback().onRemove(entity, true);

                    // delete data
                    recyclerView.post(() -> this.executeDelete(entity));
                }
            };

            if (showConfirm) {
                AlertDialogUtils.showConfirm(context, title, msg,
                        negativeButton, positiveButton, listener);
            } else {
                listener.onClick(null, DialogInterface.BUTTON_POSITIVE);
            }

        }
    }

    final void requestRecover(RecordEntity entity) {
        if (!entity.isTrash()) {
            return;
        }

        boolean result = entity.recover();
        if (result) {
            getCallback().onRemove(entity, false);
        }
    }

    final void requestShare(RecordEntity entity) {
        if (entity.isTrash() || entity.isDirectory()) {
            return;
        }

        ShareDelegate delegate = new ShareDelegate(this, entity.getId());
        requestResultManager.request(delegate);
    }

    final void requestFavorite(RecordEntity entity, boolean value) {
        FavoriteEntity ds = FavoriteEntity.obtain();
        boolean result = ds.get(entity.getId()).isPresent();
        if (!(result ^ value)) {
            return;
        }

        {
            String id = entity.getId();
            if (value) {
                ds.add(id);
            } else {
                ds.delete(id);
            }

            ds.save();
        }

        {
            String hint;
            if (value) {
                hint = "已添加到个人收藏。";
            } else {
                hint = "已从个人收藏移除。";
            }

            Snackbar.make(recyclerView, hint, Snackbar.LENGTH_SHORT).show();
        }
    }

    final void requestTag(RecordEntity entity) {
        if (entity.isTrash()) {
            return;
        }

        TagActivity.start(this, entity.getId());

        this.pendingId = Optional.ofNullable(entity.getId());
    }

    final void executeDelete(RecordEntity entity) {

        // delete from record database
        final List<RecordEntity> list = entity.delete();

        // delete database entry
        {
            FavoriteEntity favorite = FavoriteEntity.obtain();

            for (RecordEntity e : list) {
                String id = e.getId();

                if (e.isDirectory()) {

                    // delete favorite
                    favorite.delete(id);

                } else {

                    // delete recent
                    RecentEntity.delete(id);

                    // delete saved state
                    SavedStateEntity.delete(id);

                    // delete image
                    ImageEntity.deleteByDocument(id);
                }

            }
        }

        // delete file system files
        for (RecordEntity e : list) {
            if (e.isDirectory()) {
                continue;
            }

            CompletableFuture.runAsync(() -> {
                try {
                    // delete snapshot
                    File file = e.getSnapshot();
                    if (file.exists()) {
                        FileUtils.forceDelete(file);
                    }

                    // delete note document
                    file = e.getFile();
                    if (file.exists()) {
                        FileUtils.forceDelete(file);
                    }
                } catch (IOException e1) {

                }
            });
        }
    }

    MenuItemClickListener createMenuItemClickListener() {

        MenuItemClickListener listener = new MenuItemClickListener();

        listener.put(R.id.menu_add, (item) -> {
            requestCompose();
        });

        return listener;
    }

    final void scrollToEntity(final String id) {
        int index = indexOf(id);
        if (index < 0) {
            return;
        }

        index += (createNoteHeader.isChecked())? 1: 0;

        adapter.notifyItemChanged(index);

        final int position = index;
        recyclerView.post(() -> {

            View child = null;
            for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
                if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i)) == position) {
                    child = recyclerView.getChildAt(i);
                    break;
                }
            }

            if (child != null) {
                if (child.getBottom() > recyclerView.getHeight() - recyclerView.getTop()) {

                    appBarLayout.setExpanded(false, false);


                    int offset = child.getBottom() - recyclerView.getHeight();
                    recyclerView.scrollBy(0, offset);
                }
            } else {

                recyclerView.scrollToPosition(position);

                appBarLayout.setExpanded(false, false);

            }

        });
    }

    final int indexOf(String id) {
        for (int i = 0, size = sortedList.size(); i < size; i++) {
            if (sortedList.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    void addRemove(RecyclerView.ViewHolder viewHolder) {
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();

        int color = getResources().getColor(android.R.color.holo_red_light, null);
        ColorDrawable d = new ColorDrawable(color);

        View itemView = viewHolder.itemView;
        Rect rect = new Rect(0, 0, itemView.getWidth(), itemView.getHeight());
        recyclerView.offsetDescendantRectToMyCoords(itemView, rect);
        d.setBounds(rect);

        long duration = 0;
        if ((viewHolder.getAdapterPosition() + 1) == adapter.getItemCount()) {
            int offset = recyclerView.computeVerticalScrollOffset();
            if (offset < itemView.getHeight()) {
                duration = 2 * itemAnimator.getRemoveDuration();

                removeDecoration.add(d, duration, true);
            }
        }

        if (duration == 0) {
            duration = 4 * itemAnimator.getRemoveDuration();

            removeDecoration.add(d, duration);
        }

        recyclerView.invalidate();

        swipeActionHelper.setForbidden(true, duration);
    }

    void clearSwipe() {
        swipeActionHelper.clear();
    }

    /**
     *
     */
    private class RecordProvider implements ClazzAdapterProvider {

        @Override
        public Object get(int position) {
            if (createNoteHeader.isChecked()) {
                if (position == 0) {
                    return createNoteHeader;
                }

                position -= 1;
            }

            return sortedList.get(position);
        }

        @Override
        public int size() {
            int size = sortedList.size();
            size += (createNoteHeader.isChecked())? 1: 0;

            return size;
        }

        int indexOf(String id) {
            int index = -1;
            for (int i = 0, size = sortedList.size(); i < size; i++) {
                boolean found = sortedList.get(i).getId().equals(id);
                if (found) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                index += (createNoteHeader.isChecked())? 1: 0;
            }

            return index;
        }
    }

    /**
     *
     */
    private class SortedListCallback extends SortedList.Callback<RecordEntity> {

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {
            return sortEntity.getComparator().compare(o1, o2);
        }

        @Override
        public boolean areContentsTheSame(RecordEntity oldItem, RecordEntity newItem) {
            return oldItem.areContentsTheSame(newItem);
        }

        @Override
        public boolean areItemsTheSame(RecordEntity item1, RecordEntity item2) {
            return item1.getId().equals(item2.getId());
        }

        @Override
        public void onChanged(int position, int count) {
            position = getPosition(position);

            adapter.notifyItemRangeChanged(position, count);
        }

        @Override
        public void onInserted(int position, int count) {
            position = getPosition(position);

            adapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            position = getPosition(position);

            adapter.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            fromPosition = getPosition(fromPosition);
            toPosition = getPosition(toPosition);

            adapter.notifyItemMoved(fromPosition, toPosition);
        }

        int getPosition(int position) {
            return position + getHeaderCount();
        }

        int getHeaderCount() {
            return (createNoteHeader.isChecked())? 1: 0;
        }
    }

    /**
     *
     */
    private class RequestEditDelegate extends BaseRequestDelegate {

        String parentId;
        String id;

        public RequestEditDelegate(Fragment f, String parentId, String id) {
            super(f);

            this.parentId = parentId;
            this.id = id;
        }

        @Override
        public boolean request() {

            EditRecordActivity.startForResult(parent, parentId, id, requestCode);

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                return;
            }

            String id = data.getStringExtra("id");
            if (TextUtils.isEmpty(id)) {
                return;
            }

            pendingId = Optional.ofNullable(id);
        }
    }

    /**
     *
     */
    private class RequestComposeDelegate extends BaseRequestDelegate {

        RecordEntity entity;

        public RequestComposeDelegate(Fragment f, RecordEntity entity) {
            super(f);

            this.entity = entity;
        }

        @Override
        public boolean request() {
            boolean result = false;

            String id = entity.getId();

            ComposeArticleActivity.startForResult(parent, id, requestCode);
            result = true;



            return result;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                return;
            }

            String id = data.getStringExtra("id");
            if (TextUtils.isEmpty(id)) {
                return;
            }

            pendingId = Optional.ofNullable(id);
        }
    }

    /**
     *
     */
    private class RequestMoveDelegate extends BaseRequestDelegate {

        String id;

        public RequestMoveDelegate(Fragment f, String id) {
            super(f);

            this.id = id;
        }

        @Override
        public boolean request() {
            TargetFolderActivity.start(parent, id);

            return false;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {

        }
    }

    /**
     *
     */
    private static class ViewHolderCallback extends RecordViewHolder.Callback {

        BaseRecordListFragment parent;

        ViewHolderCallback(BaseRecordListFragment f) {
            this.parent = f;
        }

        @Override
        protected SwipeActionHelper getSwipeHelper() {
            return parent.swipeActionHelper;
        }

        @Override
        protected DateTime getTime(RecordEntity item) {
            return parent.getCallback().getTime(item);
        }

        @Override
        protected void onDelete(RecordViewHolder viewHolder, RecordEntity item) {

            if (item.isTrash()) {
                parent.requestDeleteNow(viewHolder, item, true);
            } else {
                parent.requestTrash(viewHolder, item);
            }

        }

        @Override
        protected void onItemClick(RecordEntity item) {
            if (item.isDirectory()) {
                FolderRecordActivity.start(parent, item.getId());
            } else {
                if (item.isTrash()) {
                    showTrashMessage(item);
                } else {
                    parent.requestCompose(item);
                }
            }
        }

        @Override
        protected boolean onItemLongClick(RecordViewHolder viewHolder, RecordEntity item) {
            int menuRes;

            if (item.isTrash()) {
                menuRes = R.menu.menu_trash;
            } else if (item.isDirectory()) {
                menuRes = R.menu.menu_folder;
            } else {
                menuRes = R.menu.menu_note;
            }

            if (menuRes == 0) {
                return false;
            }

            PopupMenu popupMenu = new PopupMenu(parent.getActivity(), viewHolder.itemView);
            popupMenu.inflate(menuRes);
            if (menuRes == R.menu.menu_folder) {
                FavoriteEntity ds = FavoriteEntity.obtain();
                if (ds.get(item.getId()).isPresent()) {
                    popupMenu.getMenu().findItem(R.id.menu_cancel_fav).setVisible(true);
                } else {
                    popupMenu.getMenu().findItem(R.id.menu_fav).setVisible(true);
                }
            }

            popupMenu.setOnMenuItemClickListener(new RecordMenuItemClickListener(parent, item));
            popupMenu.show();

            {
                SoftInputUtils.hide(parent.getActivity());
            }

            return true;
        }

        void showTrashMessage(RecordEntity entity) {
            Context context = parent.getActivity();
            CharSequence title = String.format("不能打开笔记“%1$s”，因为它在“最近删除”中。", entity.getName());
            CharSequence msg = "若要使用此项目，轻点并按住项目，然后选择“恢复”。";
            DialogInterface.OnClickListener listener = ((dialog, which) -> {});

            AlertDialogUtils.showMessage(context, title, msg, listener);

        }
    }

    /**
     *
     */
    private static class RecordMenuItemClickListener extends MenuItemClickListener {

        BaseRecordListFragment parent;
        RecordEntity entity;

        RecordMenuItemClickListener(BaseRecordListFragment f, RecordEntity entity) {

            this.parent = f;
            this.entity = entity;

            this.put(R.id.menu_rename,
                    (item) -> parent.requestEdit(entity));

            this.put(R.id.menu_move,
                    (item -> parent.requestMove(entity)));

            this.put(R.id.menu_trash,
                    item -> parent.requestTrash(null, entity));

            this.put(R.id.menu_recover,
                    item -> parent.requestRecover(entity));

            this.put(R.id.menu_delete_now,
                    item -> parent.requestDeleteNow(null, entity, true));

            this.put(R.id.menu_share,
                    (item -> parent.requestShare(entity)));

            this.put(R.id.menu_fav, item -> parent.requestFavorite(entity, true));

            this.put(R.id.menu_cancel_fav, item -> parent.requestFavorite(entity, false));

            this.put(R.id.menu_tag, item -> parent.requestTag(entity));
        }

    }

    /**
     *
     */
    private static class CreateNoteViewHolder extends SwipeViewHolder<CreateNoteHeader> {

        static final int LAYOUT_RES_ID = R.layout.layout_create_note_list_item;

        BaseRecordListFragment parent;

        @Keep
        public CreateNoteViewHolder(BaseRecordListFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);
        }

        @Override
        public void onBind(CreateNoteHeader item, int position) {
            super.onBind(item, position);
        }

        @Override
        public List<View> getTouchable(SwipeActionHelper helper) {
            return Arrays.asList(itemView);
        }

        void onItemClick(View view) {
            parent.clearSwipe();

            parent.requestCompose();
        }
    }

    /**
     *
     */
    private static class CreateNoteHeader implements Checkable {

        boolean enable;

        CreateNoteHeader(boolean enable) {
            this.enable = enable;
        }

        @Override
        public void setChecked(boolean checked) {
            this.enable = checked;
        }

        @Override
        public boolean isChecked() {
            return this.enable;
        }

        @Override
        public void toggle() {
            enable = !enable;
        }
    }

    /**
     *
     */
    private static class DefaultRecordCallback extends Callback {

        @Override
        public void onRemove(RecordEntity entity, boolean delete) {

        }
    }

    /**
     *
     *
     */
    static abstract class Callback {

        boolean createEnable;

        Callback() {
            this.createEnable = true;
        }

        public boolean isCreateEnable() {
            return createEnable;
        }

        public SortEntity getSortEntity() {
            return SortEntity.create(SortEntity.ID_NAME);
        }

        public DateTime getTime(RecordEntity entity) {
            return entity.getCreated();
        }

        public RecordEntity onCreateNote(@NonNull String style) {
            return null;
        }

        public abstract void onRemove(RecordEntity entity, boolean delete);

    }
}
