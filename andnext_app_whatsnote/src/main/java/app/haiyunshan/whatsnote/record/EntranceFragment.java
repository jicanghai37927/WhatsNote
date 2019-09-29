package app.haiyunshan.whatsnote.record;


import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.*;
import app.haiyunshan.whatsnote.*;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.base.OnRestartListener;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.config.ConfigEntity;
import app.haiyunshan.whatsnote.helper.NativeHelper;
import app.haiyunshan.whatsnote.preference.entity.PreferenceEntity;
import app.haiyunshan.whatsnote.record.entity.*;
import app.haiyunshan.whatsnote.remote.RemoteManager;
import app.haiyunshan.whatsnote.remote.RemoteService;
import app.haiyunshan.whatsnote.rxjava2.RxDisposer;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import app.haiyunshan.whatsnote.update.VersionEntry;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import club.andnext.base.BaseEntity;
import club.andnext.base.EntityList;
import club.andnext.recyclerview.animator.RemoveItemAnimator;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.decoration.RemoveDecoration;
import club.andnext.recyclerview.itemtouch.SimpleDragCallback;
import club.andnext.recyclerview.section.SectionList;
import club.andnext.recyclerview.section.SectionListAdapterCallback;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.DeleteRunner;
import club.andnext.utils.AlertDialogUtils;
import club.andnext.utils.NetworkUtils;
import club.andnext.widget.CircleColorView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntranceFragment extends Fragment implements OnRestartListener {

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    SectionList sectionList;
    MarginDividerDecoration dividerDecoration;

    RemoveItemAnimator itemAnimator;
    RemoveDecoration removeDecoration;

    ItemTouchHelper itemTouchHelper;
    ItemDragCallback itemDragCallback;

    SwipeActionHelper swipeActionHelper;

    SearchTitleBar titleBar;
    Toolbar toolbar;
    View searchBtn;
    NavigationHolder navigationHolder;

    List<BaseSection> data;
    HashMap<String, Integer> iconMap;

    boolean isEdit = false;
    int itemTransitionCounter = 0;

    RequestResultManager requestResultManager;
    RxDisposer disposer;

    public EntranceFragment() {

        {
            this.iconMap = new HashMap<>();
            iconMap.put(EntranceEntity.ID_NOTE,     R.drawable.ic_phone);
            iconMap.put(EntranceEntity.ID_RECENT,   R.drawable.ic_recent);
            iconMap.put(EntranceEntity.ID_TRASH,    R.drawable.ic_trash);
        }

        {
            this.requestResultManager = new RequestResultManager();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrance, container, false);
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
            this.removeDecoration = new RemoveDecoration(getActivity());
            recyclerView.addItemDecoration(removeDecoration);
        }

        {
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setDrawOver(false);
            dividerDecoration.setDrawTop(false);
            dividerDecoration.setLastMargin(true);
            dividerDecoration.setMargin(getResources().getDimensionPixelSize(R.dimen.entrance_item_padding));
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
            this.itemAnimator = new RemoveItemAnimator();

            RecyclerView.ItemAnimator animator = itemAnimator;
            recyclerView.setItemAnimator(animator);
        }

        {
            this.itemDragCallback = new ItemDragCallback(this);

            this.itemTouchHelper = new ItemTouchHelper(itemDragCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        {
            this.swipeActionHelper = new SwipeActionHelper();
            swipeActionHelper.setOnSwipeActionListener(new SwipeActionListener());

            swipeActionHelper.attach(recyclerView);
        }

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            this.searchBtn = titleBar.getSearchButton();
            searchBtn.setOnClickListener(this::onSearchClick);
            titleBar.getNavigationLayout().setOnClickListener(this::onNavigationClick);

            this.toolbar = titleBar.getToolbar();
            toolbar.inflateMenu(R.menu.menu_entrance);
            toolbar.setOnMenuItemClickListener(new MenuItemClickListener().put(R.id.menu_edit, this::onEditClick));

            titleBar.getEditBar().setPositiveButton(getString(R.string.btn_done), this::onDoneClick);

            this.navigationHolder = new NavigationHolder(titleBar);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.disposer = new RxDisposer(getActivity());
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new SectionListProvider());

            {
                adapter.bind(EntranceSection.class,
                        new BridgeBuilder(SectionViewHolder.class, SectionViewHolder.LAYOUT_RES_ID, this));

                adapter.bind(EntranceEntity.class,
                        new BridgeBuilder(EntranceViewHolder.class, EntranceViewHolder.LAYOUT_RES_ID, this));
            }

            {
                adapter.bind(FavoriteSection.class,
                        new BridgeBuilder(SectionViewHolder.class, SectionViewHolder.LAYOUT_RES_ID, this));

                adapter.bind(FavoriteEntity.class,
                        new BridgeBuilder(FavoriteViewHolder.class, FavoriteViewHolder.LAYOUT_RES_ID, this));
            }

            {
                adapter.bind(TagSection.class,
                        new BridgeBuilder(SectionViewHolder.class, SectionViewHolder.LAYOUT_RES_ID, this));

                adapter.bind(TagEntity.class,
                        new BridgeBuilder(TagViewHolder.class, TagViewHolder.LAYOUT_RES_ID, this));
            }

        }

        {
            this.sectionList = new SectionList(new SectionListAdapterCallback(adapter));

            BaseSection[] array = {
                    new EntranceSection(this, "位置", OptionEntity.SECTION_ENTRANCE),
                    new FavoriteSection(this, "个人收藏", OptionEntity.SECTION_FAVORITE),
                    new TagSection(this, "标签", OptionEntity.SECTION_TAG)
            };
            this.data = Arrays.asList(array);

            Arrays.stream(array).forEach(e -> {
                String key = e.getKey();
                boolean expand = OptionEntity.obtain().isSectionExpand(key);
                sectionList.add(e, expand, e);
            });
        }

        {
            Object obj = sectionList.get(sectionList.size() - 1);
            dividerDecoration.setDrawBottom(!(obj instanceof BaseSection));
        }

        {
            recyclerView.setAdapter(adapter);
        }

        {
            this.updateToolbar();
        }

        {
            UpdateVersionHelper helper = new UpdateVersionHelper();
            helper.execute();
        }

        // quickly open an activity for test.
        {
//            String id = "422c4f5e2ec84705b26124aba4fc2f5a";
//            String id = "66937fc615fc412aa59b03dffeb7a462";
//            OutlineActivity.startForResult(this, id, 1001);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRestart() {
        data.forEach(e -> e.onRestart());

        this.updateToolbar();

        {
            NativeHelper.checkValid(getActivity(), true);
        }
    }

    void onNavigationClick(View view) {
        navigationHolder.setTip(false);

        SettingActivity.start(this);
    }

    void onEditClick(int itemId) {
        this.requestEdit(true);
    }

    void onDoneClick(View view) {
        this.requestEdit(false);

        swipeActionHelper.setForbidden(false);
    }

    void onSearchClick(View view) {
        SearchRecordActivity.start(this);
    }

    void onItemEndTransition() {
        if (itemTransitionCounter == 0) {
            adapter.notifyDataSetChanged();
        }

        ++itemTransitionCounter;
    }

    void updateToolbar() {

        boolean editEnable = this.canEdit();

        toolbar.findViewById(R.id.menu_edit).setEnabled(editEnable);
    }

    boolean canEdit() {

        boolean editEnable;

        {
            FavoriteEntity ds = FavoriteEntity.obtain();
            TagEntity tagDs = TagEntity.obtain();

            editEnable = !(ds.isEmpty() && tagDs.isEmpty());
        }

        return editEnable;
    }

    void requestEdit(boolean value) {

        {
            swipeActionHelper.clear();
        }

        {
            titleBar.setEdit(value);
        }

        if (!value) {
            this.updateToolbar();
        }

        {
            swipeActionHelper.setEnable(!value);
        }

        {
            this.itemTransitionCounter = 0;

            boolean animated = false;

            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(recyclerView.getChildAt(i));
                if (h instanceof FavoriteViewHolder) {
                    FavoriteViewHolder holder = (FavoriteViewHolder)h;
                    holder.animateEdit(value);
                    animated = true;
                }
                if (h instanceof TagViewHolder) {
                    TagViewHolder holder = (TagViewHolder)h;
                    holder.animateEdit(value);
                    animated = true;
                }
            }

            if (!animated) {
                recyclerView.post(() -> adapter.notifyDataSetChanged());
            }

        }

        {
            this.isEdit = value;
        }
    }

    void requestDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
        dividerDecoration.setDragViewHolder(viewHolder);

    }

    void requestFavorite(FavoriteViewHolder viewHolder, boolean value) {
        FavoriteEntity entity = viewHolder.getItem();

        if (value) {

        } else {
            FavoriteSection section = getSection(OptionEntity.SECTION_FAVORITE);
            int index = section.indexOf(entity.getId());

            // ui data
            if (index >= 0) {

                // hide children
                viewHolder.hide();

                //
                this.addRemove(viewHolder);

                //
                section.remove(entity.getId());

                sectionList.notifyRemoved(section, index);
            }

            // logic model
            {
                FavoriteEntity ds = FavoriteEntity.obtain();
                ds.delete(entity.getId());
                ds.save();
            }

            if (!canEdit() || !isEdit) {
                requestEdit(false);
            }


        }
    }

    void requestTag(TagViewHolder viewHolder, boolean value) {
        TagEntity entity = viewHolder.getItem();

        if (value) {

        } else {
            TagSection section = getSection(OptionEntity.SECTION_TAG);
            final int index = section.indexOf(entity.getId());

            if (index >= 0) {

                // ui data
                {
                    // hide children
                    viewHolder.hide();

                    //
                    this.addRemove(viewHolder);

                    //
                    section.remove(entity.getId());

                    //
                    sectionList.notifyRemoved(section, index);
                }

                long count = entity.countOfRecord(false);
                if (count == 0) {

                    // logic model
                    {
                        entity.removeFromRecord();

                        TagEntity ds = TagEntity.obtain();
                        ds.delete(entity.getId());
                        ds.save();
                    }

                    if (!canEdit() || !isEdit) {
                        requestEdit(false);
                    }
                } else {

                    Context context = getActivity();
                    CharSequence title = String.format("标签“%1$s”将被删除并从 %2$d 项中移除。", entity.getName(), count);
                    CharSequence msg = null;
                    CharSequence negativeButton = getString(android.R.string.cancel);
                    CharSequence positiveButton = "删除标签";
                    DialogInterface.OnClickListener listener = (dialog, which) -> {
                        if (which == DialogInterface.BUTTON_POSITIVE) {

                            // logic model
                            {
                                entity.removeFromRecord();

                                TagEntity ds = TagEntity.obtain();
                                ds.delete(entity.getId());
                                ds.save();
                            }

                            if (!canEdit() || !isEdit) {
                                requestEdit(false);
                            }
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                            {
                                section.add(index, entity);
                            }

                            {
                                itemAnimator.setAddDelayExpire(300);
                                sectionList.notifyInserted(section, index);
                            }

                            if (!canEdit() || !isEdit) {
                                requestEdit(false);
                            }
                        }
                    };

                    AlertDialog dialog = AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                }
            }

        }
    }

    void requestEditTag(TagViewHolder viewHolder) {
        RequestEditTagDelegate delegate = new RequestEditTagDelegate(this, viewHolder.getItem(), entity -> {
            TagSection section = getSection(OptionEntity.SECTION_TAG);
            int index = section.data.indexOf(entity.getId());
            if (index >= 0) {
                sectionList.notifyChanged(section, index, 1, null);
            }
        });

        requestResultManager.request(delegate);
    }

    void addRemove(RecyclerView.ViewHolder viewHolder) {
        itemAnimator.setRemoveDelayExpire(100);

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

    <T> T getSection(String key) {
        return (T)(data.stream()
                .filter(s -> s.getKey().equals(key))
                .findFirst()
                .orElse(null));
    }

    /**
     *
     */
    private class SectionListProvider implements BridgeAdapterProvider {

        @Override
        public Object get(int position) {
            return sectionList.get(position);
        }

        @Override
        public int size() {
            return sectionList.size();
        }
    }

    /**
     *
     */
    private class UpdateVersionHelper {

        void execute() {
            final UpdateEntity updateEntity = UpdateEntity.obtain();

            if (shouldRequest(updateEntity)) {
                this.request(updateEntity);
            } else {
                if (updateEntity.exist()) {
                    recyclerView.post(() -> navigationHolder.setTip(true));
                } else {
                    updateEntity.deleteDownload();
                }
            }

        }

        boolean request(UpdateEntity updateEntity) {

            {
                updateEntity.setDaily(DateTime.now());
            }

            {
                io.reactivex.functions.Consumer<VersionEntry> onNext = (version) -> {
                    updateEntity.setVersion(version);
                    updateEntity.save();
                };

                io.reactivex.functions.Consumer<Throwable> onError = (version) -> {

                };

                Action onComplete = () -> {
                    if (updateEntity.exist()) {
                        navigationHolder.setTip(true);
                    } else {
                        updateEntity.deleteDownload();
                    }

                    if (updateEntity.exist()) {
                        if (PreferenceEntity.obtain().isAutoDownload()) {
                            updateEntity.download();
                        }
                    }

                };

                RemoteService service = RemoteManager.getInstance().getService();
                Observable<VersionEntry> observable = service.getVersion();
                Disposable d = observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext, onError, onComplete);
                disposer.add(d, RxDisposer.EVENT_PAUSE);

                return true;
            }
        }

        boolean shouldRequest(UpdateEntity updateEntity) {

            if (ConfigEntity.isDebug()) {
                return true;
            }

            if (!NetworkUtils.isWifiEnabled(getActivity())) {
                return false;
            }

            DateTime daily = updateEntity.getDaily().orElse(null);
            if (daily != null) {
                DateTime now = DateTime.now();
                now = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
                Period p = new Period(daily, now, PeriodType.days());
                if (p.getDays() == 0) {
                    return false;
                }
            }

            return true;
        }

    }

    /**
     *
     */
    private class NavigationHolder {

        ImageView navView;
        ImageView tipView;

        int count;

        NavigationHolder(SearchTitleBar titleBar) {
            FrameLayout layout = titleBar.getNavigationLayout();

            LayoutInflater inflater = getLayoutInflater();
            int resource = R.layout.layout_navigation_button;

            {
                ImageView view = (ImageView) inflater.inflate(resource, layout, false);
                layout.addView(view);
                view.setImageResource(R.drawable.ic_settings_white_24dp);

                this.navView = view;
            }

            {
                ImageView view = (ImageView) inflater.inflate(resource, layout, false);
                layout.addView(view);
                view.setImageResource(R.drawable.ic_update_white_24dp);

                view.setAlpha(0.f);
                this.tipView = view;

            }

            {
                this.count = 0;
            }
        }

        void setTip(boolean visible) {
            if (visible) {
                showTip();
            } else {
                hideTip();
            }
        }

        void showTip() {

            if (count > 0) {
                return;
            }

            {
                navView.animate().alpha(0).start();
            }

            {
                tipView.setTranslationY(tipView.getHeight());
                tipView.animate().alpha(1).translationY(0).start();
            }

            ++count;
        }

        void hideTip() {
            {
                navView.animate().alpha(1).start();
            }

            {
                tipView.setTranslationY(0);
                tipView.animate().alpha(0).translationY(tipView.getHeight()).start();
            }
        }
    }

    /**
     *
     */
    private class SwipeActionListener implements SwipeActionHelper.OnSwipeActionListener {

        @Override
        public void onSwipeChanged(SwipeActionHelper helper, boolean oldValue, boolean newValue) {
            if (!isEdit) {
                titleBar.setEdit(newValue);
            }
        }
    }

    /**
     *
     */
    private static class RequestEditTagDelegate extends BaseRequestDelegate {

        TagEntity entity;
        Consumer<TagEntity> consumer;

        public RequestEditTagDelegate(Fragment f, @NonNull TagEntity entity, Consumer<TagEntity> consumer) {
            super(f);

            this.entity = entity;
            this.consumer = consumer;
        }

        @Override
        public boolean request() {
            ComposeTagActivity.startForResult(parent, entity.getId(), this.getRequestCode());
            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (requestCode != Activity.RESULT_OK) {
                return;
            }

            if (data == null) {
                return;
            }

            String id = data.getStringExtra("id");
            if (TextUtils.isEmpty(id)) {
                return;
            }

            if (consumer != null) {
                consumer.accept(this.entity);
            }

        }
    }

    /**
     *
     */
    private static class ItemDragCallback extends SimpleDragCallback {

        HashMap<Class, BiConsumer<RecyclerView.ViewHolder, RecyclerView.ViewHolder>> actionMap;

        EntranceFragment parent;

        public ItemDragCallback(EntranceFragment f) {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);

            this.elevation = 12.f;

            this.parent = f;

            this.actionMap = new HashMap<>();
            actionMap.put(FavoriteViewHolder.class, (viewHolder, target) -> {
                FavoriteEntity from = ((FavoriteViewHolder)viewHolder).getItem();
                FavoriteEntity to = ((FavoriteViewHolder)target).getItem();

                // ui data
                {
                    BaseSection section = parent.getSection(OptionEntity.SECTION_FAVORITE);
                    section.move(from.getId(), to.getId());
                }

                // logic data
                {
                    FavoriteEntity ds = FavoriteEntity.obtain();
                    ds.move(from.getId(), to.getId());
                }

            });
            actionMap.put(TagViewHolder.class, (viewHolder, target) -> {
                TagEntity from = ((TagViewHolder)viewHolder).getItem();
                TagEntity to = ((TagViewHolder)target).getItem();

                // ui data
                {
                    BaseSection section = parent.getSection(OptionEntity.SECTION_TAG);
                    section.move(from.getId(), to.getId());
                }

                // logic data
                {
                    TagEntity ds = TagEntity.obtain();
                    ds.move(from.getId(), to.getId());
                }
            });
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            {
                BiConsumer consumer = actionMap.get(viewHolder.getClass());
                if (consumer != null) {
                    consumer.accept(viewHolder, target);
                }
            }

            {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                recyclerView.getAdapter().notifyItemMoved(from, to);
            }

            return true;
        }

        @Override
        public boolean canDropOver(RecyclerView recyclerView,
                                   RecyclerView.ViewHolder current,
                                   RecyclerView.ViewHolder target) {
            return (current.getClass() == target.getClass());
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            parent.dividerDecoration.setDragViewHolder(null);

            if (viewHolder == null || viewHolder instanceof FavoriteViewHolder) {
                FavoriteEntity ds = FavoriteEntity.obtain();
                ds.save();
            }

            if (viewHolder == null || viewHolder instanceof TagViewHolder) {
                TagEntity ds = TagEntity.obtain();
                ds.save();
            }
        }
    }

    /**
     *
     */
    private static class SectionViewHolder extends SwipeViewHolder<BaseSection> {

        static final int LAYOUT_RES_ID = R.layout.layout_section_list_item;

        TextView nameView;
        ImageView chevronView;

        BaseSection entity;
        EntranceFragment parent;

        @Keep
        public SectionViewHolder(EntranceFragment f, View itemView) {
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

            this.nameView = view.findViewById(R.id.tv_name);
            this.chevronView = view.findViewById(R.id.iv_chevron);

        }

        @Override
        public void onBind(BaseSection item, int position) {
            this.entity = item;

            nameView.setText(item.getName());

            if (parent.sectionList.isExpand(item)) {
                chevronView.setRotation(90);
            } else {
                chevronView.setRotation(0);
            }

            super.onBind(item, position);
        }

        @Override
        public List<View> getTouchable(SwipeActionHelper helper) {
            return Arrays.asList(itemView);
        }

        @Override
        public float getTranslation(MarginDividerDecoration decoration) {
            int width = itemView.getWidth();
            if (entity.size() != 0 && parent.sectionList.isExpand(entity)) {
                width = 0;
            }

            return width;
        }

        void onItemClick(View v) {

            boolean expand = parent.sectionList.isExpand(entity);
            parent.sectionList.setExpand(entity, !expand);
            expand = parent.sectionList.isExpand(entity);
            if (expand) {
                chevronView.animate().rotation(90);
            } else {
                chevronView.animate().rotation(0);
            }

            {
                Object obj = parent.sectionList.get(parent.sectionList.size() - 1);
                parent.dividerDecoration.setDrawBottom(!(obj instanceof BaseSection));
            }

            {
                OptionEntity.obtain().setSectionExpand(entity.getKey(), expand);
                OptionEntity.obtain().save();
            }

        }
    }

    /**
     *
     */
    private static class EntranceViewHolder extends BaseViewHolder<EntranceEntity> {

        static final int LAYOUT_RES_ID = R.layout.layout_position_entrance_list_item;

        ImageView iconView;
        TextView nameView;

        @Keep
        public EntranceViewHolder(EntranceFragment f, View itemView) {
            super(f, itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.iconView = view.findViewById(R.id.iv_icon);
            this.nameView = view.findViewById(R.id.tv_name);
        }

        @Override
        public void onBind(EntranceEntity item, int position) {

            {
                iconView.setImageResource(parent.iconMap.getOrDefault(item.getId(), R.drawable.ic_phone));
                nameView.setText(item.getName());
            }

            {
                super.onBind(item, position);
            }
        }

        void onItemClick(View v) {
            EntranceEntity item = this.getItem();
            String id = item.getId();
            switch (id) {
                case EntranceEntity.ID_NOTE: {
                    FolderRecordActivity.start(parent, RecordEntity.ROOT_NOTE);
                    break;
                }
                case EntranceEntity.ID_RECENT: {
                    RecentRecordActivity.start(parent);
                    break;
                }
                case EntranceEntity.ID_TRASH: {
                    FolderRecordActivity.start(parent, RecordEntity.ROOT_TRASH);
                    break;
                }
            }
        }


    }

    /**
     *
     */
    private static class FavoriteViewHolder extends BaseViewHolder<FavoriteEntity> {

        static final int LAYOUT_RES_ID = R.layout.layout_favorite_entrance_list_item;

        View iconView;
        TextView nameView;

        View removeBtn;
        View dragBtn;

        LayoutTransition layoutTransition;

        @Keep
        public FavoriteViewHolder(EntranceFragment f, View itemView) {
            super(f, itemView);

            {
                this.layoutTransition = new LayoutTransition();
                layoutTransition.addTransitionListener(this);

                layoutTransition.setInterpolator(LayoutTransition.APPEARING,
                        layoutTransition.getInterpolator(LayoutTransition.CHANGE_APPEARING));
                layoutTransition.setInterpolator(LayoutTransition.DISAPPEARING,
                        layoutTransition.getInterpolator(LayoutTransition.CHANGE_DISAPPEARING));

                layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
                layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
            }
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {

            {
                view.setOnClickListener(this::onItemClick);

                this.iconView = view.findViewById(R.id.iv_icon);
                this.nameView = view.findViewById(R.id.tv_name);

                this.removeBtn = itemView.findViewById(R.id.btn_remove);
                removeBtn.setOnClickListener(this::onRemoveClick);

                this.dragBtn = itemView.findViewById(R.id.btn_drag);
                dragBtn.setOnTouchListener(this::onDragTouch);
            }

            {
                SwipeHolder swipeHolder = new SwipeHolder(parent.swipeActionHelper, view, view.findViewById(R.id.swipe_view));

                {
                    View btnAction = view.findViewById(R.id.btn_delete);
                    btnAction.setOnClickListener(this::onDeleteClick);

                    DeleteRunner r = new DeleteRunner();
                    r.add(btnAction);

                    swipeHolder.add(r);
                }

                this.setSwipeHolder(swipeHolder);
            }

            view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

                if (!isEdit) {
                    removeBtn.setAlpha(0);
                    removeBtn.setTranslationX(-iconView.getRight());

                    dragBtn.setTranslationX(iconView.getRight());
                }

            });

        }

        @Override
        public void onBind(FavoriteEntity item, int position) {
            {
                nameView.setText(item.getName());
            }

            super.onBind(item, position);

        }

        @Override
        void setEdit(boolean value) {

            {
                ViewGroup layout = itemView.findViewById(R.id.swipe_view);
                layout.setLayoutTransition(null);
            }

            {
                removeBtn.setVisibility(value? View.VISIBLE: View.GONE);

                removeBtn.setTranslationX(value? 0: -removeBtn.getRight());
                removeBtn.setAlpha(value? 1: 0);
            }

            {
                dragBtn.setVisibility(value? View.VISIBLE: View.GONE);
                dragBtn.setTranslationX(value? 0: itemView.getWidth() - dragBtn.getLeft());
            }

            {
                super.setEdit(value);
            }
        }

        @Override
        public List<View> getTouchable(SwipeActionHelper helper) {
            return Arrays.asList(removeBtn);
        }

        @Override
        public void onActionEnd(SwipeActionHelper helper, int action) {
            if (action == SwipeActionHelper.ACTION_RIGHT) {
                if (getItem() != null) {
                    parent.requestFavorite(this, false);
                }
            }
        }

        @Override
        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            if (view == removeBtn) {

                Animator animator = null;
                if (transitionType == LayoutTransition.APPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.ALPHA, 1.f),
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0.f));

                } else if (transitionType == LayoutTransition.DISAPPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.ALPHA, 0.f),
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -view.getRight()));

                }

                transition.setAnimator(transitionType, animator);
            }

            if (view == dragBtn) {

                ObjectAnimator animator = null;
                if (transitionType == LayoutTransition.APPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0));

                } else if (transitionType == LayoutTransition.DISAPPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, itemView.getWidth() - view.getLeft()));
                }

                transition.setAnimator(transitionType, animator);
            }
        }

        @Override
        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
            if (view == removeBtn) {
                if (transitionType == LayoutTransition.DISAPPEARING) {
                    view.setAlpha(0);
                }
            }

            super.endTransition(transition, container, view, transitionType);
        }

        void animateEdit(boolean value) {

            {
                ViewGroup layout = itemView.findViewById(R.id.swipe_view);
                if (layout.getLayoutTransition() == null) {
                    layout.setLayoutTransition(layoutTransition);
                }
            }

            {
                removeBtn.setVisibility(value? View.VISIBLE: View.GONE);
            }

            {
                dragBtn.setVisibility(value? View.VISIBLE: View.GONE);
            }
        }

        void onItemClick(View view) {
            FavoriteEntity item = getItem();
            FolderRecordActivity.start(parent, item.getId());
        }

        void onRemoveClick(View view) {
            getSwipeHolder().expand(SwipeActionHelper.DIRECTION_RTL, true);
        }

        void onDeleteClick(View view) {
            if (getItem() != null) {
                parent.requestFavorite(this, false);
            }
        }

        boolean onDragTouch(View view, MotionEvent event) {
            parent.requestDrag(this);

            return false;
        }
    }

    /**
     *
     */
    private static class TagViewHolder extends BaseViewHolder<TagEntity> {

        static final int LAYOUT_RES_ID = R.layout.layout_tag_entrance_list_item;

        CircleColorView iconView;
        TextView nameView;

        View removeBtn;
        View dragBtn;

        LayoutTransition layoutTransition;

        @Keep
        public TagViewHolder(EntranceFragment f, View itemView) {
            super(f, itemView);

            {
                this.layoutTransition = new LayoutTransition();
                layoutTransition.addTransitionListener(this);

                layoutTransition.setInterpolator(LayoutTransition.APPEARING,
                        layoutTransition.getInterpolator(LayoutTransition.CHANGE_APPEARING));
                layoutTransition.setInterpolator(LayoutTransition.DISAPPEARING,
                        layoutTransition.getInterpolator(LayoutTransition.CHANGE_DISAPPEARING));

                layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
                layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
            }
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            {
                view.setOnClickListener(this::onItemClick);

                this.iconView = view.findViewById(R.id.iv_icon);
                this.nameView = view.findViewById(R.id.tv_name);

                nameView.setOnClickListener(this::onNameClick);

                this.removeBtn = itemView.findViewById(R.id.btn_remove);
                removeBtn.setOnClickListener(this::onRemoveClick);

                this.dragBtn = itemView.findViewById(R.id.btn_drag);
                dragBtn.setOnTouchListener(this::onDragTouch);
            }

            {
                SwipeHolder swipeHolder = new SwipeHolder(parent.swipeActionHelper, view, view.findViewById(R.id.swipe_view));

                {
                    View btnAction = view.findViewById(R.id.btn_delete);
                    btnAction.setOnClickListener(this::onDeleteClick);

                    DeleteRunner r = new DeleteRunner();
                    r.add(btnAction);

                    swipeHolder.add(r);
                }

                this.setSwipeHolder(swipeHolder);
            }

            view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

                if (!isEdit) {
                    removeBtn.setAlpha(0);
                    removeBtn.setTranslationX(-iconView.getRight());

                    dragBtn.setTranslationX(iconView.getRight());
                }

            });

        }

        @Override
        public void onBind(TagEntity item, int position) {

            {
                iconView.setColor(item.getColor().getIcon());
                nameView.setText(item.getName());
            }

            {
                super.onBind(item, position);
            }
        }

        @Override
        void setEdit(boolean value) {

            {
                ViewGroup layout = itemView.findViewById(R.id.swipe_view);
                layout.setLayoutTransition(null);
            }

            {
                nameView.setClickable(value);
            }

            {
                removeBtn.setVisibility(value? View.VISIBLE: View.GONE);

                removeBtn.setTranslationX(value? 0: -removeBtn.getRight());
                removeBtn.setAlpha(value? 1: 0);
            }

            {
                dragBtn.setVisibility(value? View.VISIBLE: View.GONE);
                dragBtn.setTranslationX(value? 0: itemView.getWidth() - dragBtn.getLeft());
            }

            {
                super.setEdit(value);
            }
        }

        @Override
        public List<View> getTouchable(SwipeActionHelper helper) {
            return Arrays.asList(removeBtn);
        }

        @Override
        public void onActionEnd(SwipeActionHelper helper, int action) {
            if (action == SwipeActionHelper.ACTION_RIGHT) {
                if (getItem() != null) {
                    parent.requestTag(this, false);
                }
            }
        }

        @Override
        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            if (view == removeBtn) {

                Animator animator = null;
                if (transitionType == LayoutTransition.APPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.ALPHA, 1.f),
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0.f));

                } else if (transitionType == LayoutTransition.DISAPPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.ALPHA, 0.f),
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -view.getRight()));

                }

                transition.setAnimator(transitionType, animator);
            }

            if (view == dragBtn) {

                ObjectAnimator animator = null;
                if (transitionType == LayoutTransition.APPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0));

                } else if (transitionType == LayoutTransition.DISAPPEARING) {
                    animator = ObjectAnimator.ofPropertyValuesHolder(view,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, itemView.getWidth() - view.getLeft()));
                }

                transition.setAnimator(transitionType, animator);
            }
        }

        @Override
        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
            if (view == removeBtn) {
                if (transitionType == LayoutTransition.DISAPPEARING) {
                    view.setAlpha(0);
                }
            }

            super.endTransition(transition, container, view, transitionType);
        }

        void animateEdit(boolean value) {

            {
                ViewGroup layout = itemView.findViewById(R.id.swipe_view);
                if (layout.getLayoutTransition() == null) {
                    layout.setLayoutTransition(layoutTransition);
                }
            }

            {
                removeBtn.setVisibility(value? View.VISIBLE: View.GONE);
            }

            {
                dragBtn.setVisibility(value? View.VISIBLE: View.GONE);
            }
        }

        public void onItemClick(View v) {
            TagEntity item = getItem();
            TagRecordActivity.start(parent, item.getId());
        }

        void onNameClick(View view) {
            if (getItem() != null) {
                parent.requestEditTag(this);
            }
        }

        void onRemoveClick(View view) {
            getSwipeHolder().expand(SwipeActionHelper.DIRECTION_RTL, true);
        }

        void onDeleteClick(View view) {
            if (getItem() != null) {
                parent.requestTag(this, false);
            }
        }

        boolean onDragTouch(View view, MotionEvent event) {
            parent.requestDrag(this);

            return false;
        }
    }

    /**
     *
     * @param <T>
     */
    private static abstract class BaseViewHolder<T> extends SwipeViewHolder<T> implements LayoutTransition.TransitionListener {

        Boolean isEdit = null;

        EntranceFragment parent;

        @Keep
        public BaseViewHolder(EntranceFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @CallSuper
        @Override
        public void onBind(T item, int position) {
            super.onBind(item, position);

            this.setEdit(parent.isEdit);
        }

        @Override
        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

        }

        @CallSuper
        @Override
        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
            if (transitionType == LayoutTransition.CHANGE_APPEARING
                || transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                parent.onItemEndTransition();
            }
        }

        @CallSuper
        void setEdit(boolean value) {

            {
                itemView.setClickable(!value);
            }

            {
                this.isEdit = value;
            }
        }

    }

    /**
     *
     */
    private static class EntranceSection extends BaseSection {

        EntranceSection(EntranceFragment f, String name, String key) {
            super(f, name, key);

            this.data = EntranceEntity.obtain().toList();
        }
    }

    /**
     *
     */
    private static class FavoriteSection extends BaseSection {

        FavoriteSection(EntranceFragment f, String name, String key) {
            super(f, name, key);

            this.data = FavoriteEntity.obtain().toList();
            this.oldData = null;
        }

        @Override
        void onRestart() {
            this.data = FavoriteEntity.obtain().toList();

            super.onRestart();
        }
    }

    /**
     *
     */
    private static class TagSection extends BaseSection {

        TagSection(EntranceFragment f, String name, String key) {
            super(f, name, key);

            this.data = TagEntity.obtain().toList();
            this.oldData = null;
        }

        @Override
        void onRestart() {
            this.data = TagEntity.obtain().toList();

            super.onRestart();
        }

    }

    /**
     *
     */
    private static abstract class BaseSection implements BridgeAdapterProvider<BaseEntity>, LifecycleObserver {

        String name;
        String key;

        EntityList data;
        EntityList oldData;

        EntranceFragment parent;

        BaseSection(EntranceFragment f, String name, String key) {
            this.parent = f;
            this.name = name;
            this.key = key;

            f.getLifecycle().addObserver(this);
        }

        String getName() {
            return name;
        }

        String getKey() { return key; }

        public void remove(String id) {
            data.remove(id);
        }

        public int indexOf(String id) {
            return data.indexOf(id);
        }

        public void add(int index, BaseEntity entity) {
            data.add(index, entity);
        }

        public boolean move(String fromId, String toId) {
            return data.move(fromId, toId);
        }

        @Override
        public BaseEntity get(int position) {
            return data.get(position);
        }

        @Override
        public int size() {
            return data.size();
        }

        void onRestart() {
            if (parent.sectionList.isExpand(this)) {
                if (data != null && oldData != null) {
                    DiffUtil.calculateDiff(new DiffCallback(data, oldData))
                            .dispatchUpdatesTo(new DiffListUpdateCallback(parent.sectionList, this));
                }

                oldData = null;
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onStop() {
            this.oldData = null;

            if (parent.sectionList.isExpand(this)) {
                oldData = data;
            }
        }
    }

    /**
     *
     */
    private static class DiffListUpdateCallback implements ListUpdateCallback {

        SectionList sectionList;
        BaseSection section;

        DiffListUpdateCallback(SectionList sectionList, BaseSection section) {
            this.sectionList = sectionList;
            this.section = section;
        }

        @Override
        public void onInserted(int position, int count) {
            sectionList.notifyInserted(section, position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            sectionList.notifyRemoved(section, position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            sectionList.notifyMoved(section, fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
            sectionList.notifyChanged(section, position, count, payload);
        }
    }

    /**
     *
     */
    private static class DiffCallback extends DiffUtil.Callback {

        EntityList data;
        EntityList oldData;

        DiffCallback(EntityList data, EntityList oldData) {
            this.data = data;
            this.oldData = oldData;
        }

        @Override
        public int getOldListSize() {
            return oldData.size();
        }

        @Override
        public int getNewListSize() {
            return data.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            BaseEntity oldItem = oldData.get(oldItemPosition);
            BaseEntity newItem = data.get(newItemPosition);

            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            BaseEntity oldItem = oldData.get(oldItemPosition);
            BaseEntity newItem = data.get(newItemPosition);

            return oldItem.areContentsTheSame(newItem);
        }
    }



}
