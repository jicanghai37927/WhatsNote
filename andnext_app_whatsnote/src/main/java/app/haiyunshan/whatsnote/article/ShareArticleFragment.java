package app.haiyunshan.whatsnote.article;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.ProfileActivity;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.*;
import app.haiyunshan.whatsnote.article.share.*;
import app.haiyunshan.whatsnote.article.viewholder.ComposeViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.FormulaViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.PictureViewHolder;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.page.item.MasterItem;
import app.haiyunshan.whatsnote.page.viewholder.MasterViewHolder;
import app.haiyunshan.whatsnote.preference.entity.PreferenceEntity;
import app.haiyunshan.whatsnote.preference.entity.ProfileEntity;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.setting.item.PictureSettingItem;
import app.haiyunshan.whatsnote.setting.item.SeparatorSettingItem;
import app.haiyunshan.whatsnote.setting.viewholder.PictureSettingViewHolder;
import app.haiyunshan.whatsnote.setting.viewholder.SeparatorSettingViewHolder;
import app.haiyunshan.whatsnote.share.ShareByDialogFragment;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import club.andnext.dialog.PopupMenuDialogFragment;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.drawable.BackgroundDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareArticleFragment extends Fragment {

    SearchTitleBar titleBar;
    View shareBtn;

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    ComposeProvider provider;

    ItemTouchHelper itemTouchHelper;

    ComposeCallback composeCallback;

    MasterHelper masterHelper;

    Document document;
    RecordEntity recordEntity;

    RequestResultManager requestResultManager;

    public ShareArticleFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.composeCallback = new ComposeCallback(this);
        this.requestResultManager = new RequestResultManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);

            Toolbar toolbar = titleBar.getToolbar();
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(this::onBackClick);

            toolbar.inflateMenu(R.menu.menu_master);
            toolbar.setOnMenuItemClickListener(new MenuItemClickListener().put(R.id.menu_author, this::onAuthorClick));
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            Drawable d = getActivity().getDrawable(R.drawable.anc_shape_texture_paper);
            Drawable[] layers = new Drawable[] {
                    new BackgroundDrawable(recyclerView, d)
            };

            recyclerView.setBackground(new LayerDrawable(layers));
        }

        {
            this.itemTouchHelper = new ItemTouchHelper(new SwipeCallback(this));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        {
            View card = view.findViewById(R.id.content_card);
            card.setClipToOutline(true);
        }

        {
            this.shareBtn = view.findViewById(R.id.btn_share);
            shareBtn.setOnClickListener(this::onShareClick);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = getArguments();
            String id = args.getString("id");

            this.document = Document.create(id, true);
            document.setReadOnly(true);
            this.recordEntity = RecordEntity.create(id).get();
        }

        {
            titleBar.setTitle(recordEntity.getName());
        }

        {
            this.provider = new ComposeProvider(document);
            this.adapter = new BridgeAdapter(getActivity(), provider);

            {
                adapter.bind(ParagraphEntity.class,
                        new BridgeBuilder(ParagraphViewHolder.class, ParagraphViewHolder.LAYOUT_RES_ID, composeCallback)
                                .setParameterTypes(ComposeViewHolder.Callback.class));

                adapter.bind(PictureEntity.class,
                        new BridgeBuilder(PictureViewHolder.class, PictureViewHolder.LAYOUT_RES_ID, composeCallback)
                                .setParameterTypes(ComposeViewHolder.Callback.class));

                adapter.bind(FormulaEntity.class,
                        new BridgeBuilder(FormulaViewHolder.class, FormulaViewHolder.LAYOUT_RES_ID, composeCallback)
                                .setParameterTypes(ComposeViewHolder.Callback.class));
            }

            {
                adapter.bind(SeparatorSettingItem.class,
                        new BridgeBuilder(SeparatorSettingViewHolder.class, SeparatorSettingViewHolder.LAYOUT_RES_ID));

                adapter.bind(MasterItem.class,
                        new BridgeBuilder(MasterViewHolder.class, MasterViewHolder.LAYOUT_RES_ID));

                adapter.bind(PictureSettingItem.class,
                        new BridgeBuilder(VisionViewHolder.class, VisionViewHolder.LAYOUT_RES_ID));
            }
        }

        {
            this.masterHelper = new MasterHelper(this, PreferenceEntity.obtain().getProfile());
            masterHelper.attach(provider);

        }

        {
            recyclerView.setAdapter(adapter);
            recyclerView.post(() -> recyclerView.scrollToPosition(0));
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    void onBackClick(View view) {
        getActivity().onBackPressed();
    }

    void onShareClick(View view) {
        ShareByDialogFragment dialogFragment = new ShareByDialogFragment();

        dialogFragment.setTheme(R.style.DialogDimTheme);
        dialogFragment.setMenuResource(R.menu.menu_share);

        {
            Intent[] intents = new Intent[] {
                    new Intent().setAction(Intent.ACTION_SEND).setType("*/*"),
                    new Intent().setAction(Intent.ACTION_SEND).setType("image/*"),
                    new Intent().setAction(Intent.ACTION_SEND_MULTIPLE).setType("*/*")
            };

            dialogFragment.setIntent(intents);
        }

        {
            MenuItemClickListener listener = new MenuItemClickListener();
            listener.put(R.id.menu_picture, item -> {

                    SharePictureAction action = new SharePictureAction(getActivity(), recyclerView, recordEntity, document);
                    action.execute();

            });

            listener.put(R.id.menu_plain_text, item -> {

                    ShareTextAction action = new ShareTextAction(getActivity(), recyclerView, recordEntity, document);
                    action.execute();

            });

            listener.put(R.id.menu_markdown, item -> {

                    ShareMarkdownAction action = new ShareMarkdownAction(getActivity(), recyclerView, recordEntity, document);
                    action.execute();

            });

            listener.put(R.id.menu_hexo, item -> {

                    ShareHexoAction action = new ShareHexoAction(getActivity(), recyclerView, recordEntity, document);
                    action.execute();

            });

            dialogFragment.setOnMenuItemClickListener(listener);
        }

        {
            ShareByDialogFragment.OnResolveItemClickListener listener = (resolveInfo -> {
                ShareIntentAction action = new ShareIntentAction(getActivity(), recyclerView, recordEntity, document, resolveInfo);
                action.execute();
            });

            dialogFragment.setOnResolveItemClickListener(listener);
        }

        dialogFragment.showNow(getChildFragmentManager(), "share");
    }

    void onAuthorClick(int itemId) {

        int[] exclude;
        MenuItemClickListener listener = new MenuItemClickListener();

        {
            listener.put(R.id.menu_visible, e -> {
                requestMaster(true);
            });

            listener.put(R.id.menu_gone, e -> {
                requestMaster(false);
            });

            listener.put(R.id.menu_edit, e -> {
                requestEditProfile();
            });
        }

        if (masterHelper.isVisible()) {
            exclude = new int[] { R.id.menu_visible };
        } else {
            exclude = new int[] { R.id.menu_gone };
        }

        {
            PopupMenuDialogFragment dialogFragment = new PopupMenuDialogFragment();
            dialogFragment.setTheme(R.style.DialogDimTheme);
            dialogFragment.setMenuResource(R.menu.menu_author, exclude);
            dialogFragment.setOnMenuItemClickListener(listener);

            dialogFragment.showNow(getFragmentManager(), "author");
        }
    }

    void requestMaster(boolean visible) {
        masterHelper.setVisible(visible);
    }

    void requestEditProfile() {
        RequestEditProfileDelegate delegate = new RequestEditProfileDelegate(this, (data) -> {
            masterHelper.set(PreferenceEntity.obtain().getProfile());
        });

        requestResultManager.request(delegate);
    }

    void requestSwipe(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction != ItemTouchHelper.LEFT) {
            return;
        }

        int position = viewHolder.getAdapterPosition();

        if (viewHolder instanceof MasterViewHolder) {
            Object obj = masterHelper.masterItem;
            int index = provider.indexOf(obj);
            if (index >= 0) {
                provider.remove(index);
                adapter.notifyItemRemoved(index);
            }
        } else if (viewHolder instanceof VisionViewHolder) {
            Object obj = masterHelper.visionItem;
            int index = provider.indexOf(obj);
            if (index >= 0) {
                provider.remove(index);
                adapter.notifyItemRemoved(index);
            }
        } else {
            Object obj = adapter.get(position);
            if (obj instanceof DocumentEntity && document.indexOf((DocumentEntity)obj) >= 0) {
                int index = provider.indexOf(obj);
                if (index >= 0) {
                    document.remove((DocumentEntity)obj); // remove from document

                    provider.remove(index);
                    adapter.notifyItemRemoved(index);
                }


            }
        }

        if (masterHelper.isGone()) {
            Object obj = masterHelper.separatorItem;
            int index = provider.indexOf(obj);
            if (index >= 0) {
                provider.remove(index);
                adapter.notifyItemRemoved(index);
            }
        }

    }

    /**
     *
     */
    private static class ComposeProvider implements BridgeAdapterProvider<Object> {

        ArrayList<Object> list;

        ComposeProvider(Document document) {
            this.list = new ArrayList<>(document.getList());
        }

        void add(Object object) {
            list.add(object);
        }

        void add(int index, Object object) {
            list.add(index, object);
        }

        int indexOf(Object object) {
            return list.indexOf(object);
        }

        Object remove(int index) {
            return list.remove(index);
        }

        @Override
        public Object get(int position) {
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
    private static class MasterHelper {

        ShareArticleFragment parent;

        SeparatorSettingItem separatorItem;
        MasterItem masterItem;
        PictureSettingItem visionItem;

        MasterHelper(ShareArticleFragment f, ProfileEntity profile) {
            this.parent = f;

//            this.separatorItem = new SeparatorSettingItem(f.getActivity().getResources().getDimensionPixelSize(R.dimen.shareProfileMargin));
            this.separatorItem = new SeparatorSettingItem(0);

            this.masterItem = new MasterItem(profile, this::onPortraitClick);

            int width = f.getActivity().getResources().getDisplayMetrics().widthPixels;
            int height = width * ProfileEntity.VISION_HEIGHT / ProfileEntity.VISION_WIDTH;
            this.visionItem = new PictureSettingItem(profile.getVisionUri(), width, height);
            visionItem.setPlaceholderResId(R.drawable.ic_profile_vision);
            visionItem.setErrorResId(R.drawable.ic_profile_vision);
            visionItem.setConsumer(this::onVisionClick);
        }

        void attach(ComposeProvider provider) {
            provider.add(separatorItem);
            provider.add(masterItem);
            provider.add(visionItem);
        }

        boolean isVisible() {
            ComposeProvider provider = parent.provider;

            int a = provider.indexOf(masterItem);
            int b = provider.indexOf(visionItem);

            return (a >= 0) && (b >= 0);
        }

        boolean isGone() {
            ComposeProvider provider = parent.provider;

            int a = provider.indexOf(masterItem);
            int b = provider.indexOf(visionItem);

            return (a < 0) && (b < 0);
        }

        void setVisible(boolean visible) {
            if (visible) {
                setVisible();
            } else {
                setGone();
            }
        }

        void setGone() {
            ComposeProvider provider = parent.provider;

            int pos = provider.indexOf(separatorItem);

            if (pos >= 0) {
                int count = provider.size() - pos;

                for (int i = provider.size() - 1; i >= pos; i--) {
                    provider.remove(i);
                }

                parent.adapter.notifyItemRangeRemoved(pos, count);
            }
        }

        void setVisible() {
            ComposeProvider provider = parent.provider;

            if (isGone()) {
                int index = provider.size();

                provider.add(separatorItem);
                provider.add(masterItem);
                provider.add(visionItem);

                parent.adapter.notifyItemRangeInserted(index, 3);
            } else {
                if (provider.indexOf(masterItem) < 0) {
                    int index = provider.indexOf(visionItem);

                    provider.add(index, masterItem);
                    parent.adapter.notifyItemInserted(index);
                }

                if (provider.indexOf(visionItem) < 0) {
                    int index = provider.indexOf(masterItem) + 1;

                    provider.add(index, visionItem);
                    parent.adapter.notifyItemInserted(index);
                }

            }
        }

        void set(ProfileEntity profile) {

            {
                masterItem.set(profile);
                visionItem.setIconUri(profile.getVisionUri());
            }


            ComposeProvider provider = parent.provider;

            Arrays.asList(masterItem, visionItem).stream()
                    .map(object -> provider.indexOf(object))
                    .filter(index -> index >= 0)
                    .forEach(pos -> parent.adapter.notifyItemChanged(pos));

        }

        void onPortraitClick(MasterItem item) {
            parent.requestEditProfile();
        }

        void onVisionClick(Object object) {
            parent.requestEditProfile();
        }
    }

    /**
     *
     */
    private static class ComposeCallback extends ComposeViewHolder.Callback {

        ShareArticleFragment parent;

        ComposeCallback(ShareArticleFragment f) {
            super(f.getActivity());

            this.parent = f;
            this.enable = false;
        }

        @Override
        public void remove(ComposeViewHolder holder) {

        }

        @Override
        public void view(ComposeViewHolder viewHolder) {

        }

        @Override
        public void compose(ComposeViewHolder viewHolder) {

        }

        @Override
        public int getMaxWidth() {
            return super.getMaxWidth();
        }

        @Override
        public int getMaxHeight() {
            return super.getMaxHeight();
        }
    }

    /**
     *
     */
    private static class VisionViewHolder extends PictureSettingViewHolder {

        public static final int LAYOUT_RES_ID = R.layout.layout_vision_list_item;

        @Keep
        public VisionViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            super.onViewCreated(view);

            iconView.setOnClickListener(this::onItemClick);
        }
    }

    /**
     *
     */
    private static class SwipeCallback extends ItemTouchHelper.Callback {

        ShareArticleFragment parent;

        SwipeCallback(ShareArticleFragment f) {
            this.parent = f;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            boolean enable = (viewHolder instanceof MasterViewHolder) || (viewHolder instanceof VisionViewHolder);

            // enable document swipe
            if (!enable) {
                int pos = viewHolder.getAdapterPosition();
                enable = pos < parent.document.size();
            }

            int dragDirs = 0;
            int swipeDirs = (enable)? ItemTouchHelper.LEFT: 0;

            return makeMovementFlags(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            parent.requestSwipe(viewHolder, direction);
        }
    }

    /**
     *
     */
    private static class RequestEditProfileDelegate extends BaseRequestDelegate {

        Consumer consumer;

        public RequestEditProfileDelegate(Fragment f, Consumer consumer) {
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
