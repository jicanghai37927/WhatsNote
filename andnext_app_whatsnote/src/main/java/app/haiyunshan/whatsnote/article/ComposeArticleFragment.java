package app.haiyunshan.whatsnote.article;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.ComposeArticleActivity;
import app.haiyunshan.whatsnote.OutlineActivity;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.delegate.*;
import app.haiyunshan.whatsnote.article.deletion.BaseDeletion;
import app.haiyunshan.whatsnote.article.entity.*;
import app.haiyunshan.whatsnote.article.helper.DeletionHelper;
import app.haiyunshan.whatsnote.article.helper.ComposeHelper;
import app.haiyunshan.whatsnote.article.helper.SaveHelper;
import app.haiyunshan.whatsnote.article.helper.UndoHelper;
import app.haiyunshan.whatsnote.article.insertion.BaseInsertion;
import app.haiyunshan.whatsnote.article.insertion.ParagraphInsertion;
import app.haiyunshan.whatsnote.article.share.ShareClipboardAction;
import app.haiyunshan.whatsnote.article.viewholder.ComposeViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.FormulaViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.PictureViewHolder;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import club.andnext.dialog.PopupMenuDialogFragment;
import club.andnext.helper.SoftInputHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.drawable.BackgroundDrawable;
import club.andnext.recyclerview.helper.EditTouchHelper;
import club.andnext.utils.SoftInputUtils;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeArticleFragment extends Fragment {

    static final String TAG = ComposeArticleFragment.class.getSimpleName();

    Document document;

    Toolbar toolbar;
    View backBtn;
    View doneBtn;
    View undoBtn;
    View redoBtn;

    RecyclerView recyclerView;

    BridgeAdapter adapter;

    ComposeCallback composeCallback;
    InsertionCallback insertionCallback;
    DeletionCallback deletionCallback;

    ComposeHelper composeHelper;
    SaveHelper saveHelper;
    ComposeTouchHelper composeTouchHelper;
    DeletionHelper deletionHelper;
    SoftInputHelper softInputHelper;

    ViewEntityHelper viewEntityHelper;
    ComposeEntityHelper composeEntityHelper;

    UndoHelper undoHelper;

    ComposeSoftInputListener softInputListener;

    RequestResultManager requestResultManager;

    public ComposeArticleFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.composeCallback = new ComposeCallback(this);
        this.insertionCallback = new InsertionCallback(this);
        this.deletionCallback = new DeletionCallback(this);
        this.softInputListener = new ComposeSoftInputListener();

        this.deletionHelper = new DeletionHelper(deletionCallback);

        this.viewEntityHelper = new ViewEntityHelper(this);
        this.composeEntityHelper = new ComposeEntityHelper(this);

        this.requestResultManager = new RequestResultManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.toolbar = view.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_compose_article);
            toolbar.setOnMenuItemClickListener(new ComposeMenuItemListener(this));
        }

        {
            this.backBtn = view.findViewById(R.id.iv_back);
            backBtn.setOnClickListener(this::onBackClick);

            this.doneBtn = view.findViewById(R.id.tv_done);
            doneBtn.setOnClickListener(this::onDoneClick);

            this.undoBtn = view.findViewById(R.id.btn_undo);

            this.redoBtn = view.findViewById(R.id.btn_redo);
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
            ComposeScrollListener scrollListener = new ComposeScrollListener();
            recyclerView.addOnScrollListener(scrollListener);
        }

        {
//            recyclerView.getItemAnimator().setChangeDuration(0);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = args.getString("id");

            this.document = Document.create(id);

            Log.v(TAG, "article id = " + id);
        }

        {
            Intent intent = new Intent();
            intent.putExtra("id", document.getId());

            getActivity().setResult(RESULT_OK, intent);
        }

        {
            this.composeTouchHelper = new ComposeTouchHelper();
            composeTouchHelper.attach(recyclerView);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new ComposeProvider());

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
            recyclerView.setAdapter(adapter);
        }

        {
            this.softInputHelper = new SoftInputHelper(this.getActivity());

            this.composeHelper = new ComposeHelper(this, document, recyclerView, softInputHelper);
            this.saveHelper = new SaveHelper(composeHelper);
            if (!composeHelper.showSoftInput()) {
                saveHelper.restore();
            }
        }

        {
            this.undoHelper = new UndoHelper(this, undoBtn, redoBtn);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        softInputHelper.addOnSoftInputListener(softInputListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        saveHelper.save();
    }

    @Override
    public void onStop() {
        super.onStop();

        softInputHelper.removeOnSoftInputListener(softInputListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (composeHelper != null) {
            composeHelper.recycle();
        }
    }

    void onBackClick(View view) {
        getActivity().onBackPressed();
    }

    void onDoneClick(View view) {
        SoftInputUtils.hide(getActivity());

        doneBtn.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
    }

    void requestInsertion() {

        MenuItemClickListener listener = new MenuItemClickListener();

        {

            listener.put(R.id.menu_insert_paragraph, e ->
                    new ParagraphInsertion(insertionCallback).execute()
            );

            listener.put(R.id.menu_camera, e -> {
                CameraDelegate delegate = new CameraDelegate(this, insertionCallback);
                requestResultManager.request(delegate);
            });

            listener.put(R.id.menu_photo, e -> {
                PhotoDelegate delegate = new PhotoDelegate(this, insertionCallback);
                requestResultManager.request(delegate);
            });

            listener.put(R.id.menu_insert_formula, e -> {

                    FormulaDelegate delegate = new FormulaDelegate(this, null, insertionCallback);
                    requestResultManager.request(delegate);

            });
        }

        {
            PopupMenuDialogFragment dialogFragment = new PopupMenuDialogFragment();
            dialogFragment.setTheme(R.style.DialogDimTheme);
            dialogFragment.setMenuResource(R.menu.menu_compose_insertion);
            dialogFragment.setOnMenuItemClickListener(listener);

            dialogFragment.showNow(getFragmentManager(), "insertion");
        }
    }

    void requestMore() {

        MenuItemClickListener listener = new MenuItemClickListener();

        {
            listener.put(R.id.menu_share, e -> {
                ShareDelegate d = new ShareDelegate(this, this.composeHelper);
                this.requestResultManager.request(d);
            });

            listener.put(R.id.menu_outline, e -> {

                    OutlineDelegate delegate = new OutlineDelegate(this);
                    requestResultManager.request(delegate);

            });

            listener.put(R.id.menu_copy, e -> {
                ShareClipboardAction action = new ShareClipboardAction(getActivity(), recyclerView, composeHelper.getEntity(), composeHelper.getDocument());
                action.execute();
            });
        }

        {
            PopupMenuDialogFragment dialogFragment = new PopupMenuDialogFragment();
            dialogFragment.setTheme(R.style.DialogDimTheme);
            dialogFragment.setMenuResource(R.menu.menu_compose_more);
            dialogFragment.setOnMenuItemClickListener(listener);

            dialogFragment.showNow(getFragmentManager(), "more");
        }
    }

    /**
     *
     */
    private class ComposeProvider implements BridgeAdapterProvider<DocumentEntity> {

        @Override
        public DocumentEntity get(int position) {
            return document.get(position);
        }

        @Override
        public int size() {
            return document.size();
        }
    }

    /**
     *
     */
    private static class OutlineDelegate extends BaseRequestDelegate {

        ComposeArticleFragment parent;

        public OutlineDelegate(ComposeArticleFragment f) {
            super(f);

            this.parent = f;
        }

        @Override
        public boolean request() {
            OutlineActivity.startForResult(parent, parent.document.getId(), getRequestCode());

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode != RESULT_OK) {
                return;
            }

            Context context = parent.getActivity();
            if (context instanceof ComposeArticleActivity) {
                parent.saveHelper.clearSavedState(); // must clear saved state first

                parent.document.setReadOnly(true); // and then set read only
                ((ComposeArticleActivity)context).requestCompose(parent.document.getId());
            }
        }
    }

    /**
     *
     */
    private static class DeletionCallback extends BaseDeletion.Callback {

        Activity context;
        ComposeArticleFragment parent;

        DeletionCallback(ComposeArticleFragment f) {
            this.parent = f;
            this.context = f.getActivity();
        }

        @Override
        public Activity getActivity() {
            return context;
        }

        @Override
        public RecyclerView getRecyclerView() {
            return parent.recyclerView;
        }

        @Override
        public Document getDocument() {
            return parent.document;
        }
    }

    /**
     *
     */
    private static class InsertionCallback extends BaseInsertion.Callback {

        Activity context;
        ComposeArticleFragment parent;

        InsertionCallback(ComposeArticleFragment f) {
            this.parent = f;
            this.context = f.getActivity();
        }

        @Override
        public Activity getActivity() {
            return this.context;
        }

        @Override
        public RecyclerView getRecyclerView() {
            return parent.recyclerView;
        }

        @Override
        public Document getDocument() {
            return parent.document;
        }
    }

    /**
     *
     */
    private class ComposeSoftInputListener implements SoftInputHelper.OnSoftInputListener {

        @Override
        public void onSoftInputChanged(SoftInputHelper helper, boolean visible) {
            backBtn.setVisibility(visible? View.GONE: View.VISIBLE);
            doneBtn.setVisibility(visible? View.VISIBLE: View.GONE);

            if (!visible) {
                saveHelper.saveDocumentOnly();
            }

            IntStream.range(0, recyclerView.getChildCount())
                    .mapToObj(i -> recyclerView.getChildAt(i))
                    .map(v -> recyclerView.findContainingViewHolder(v))
                    .map(h -> (ComposeViewHolder)h)
                    .forEach(h -> h.onSoftInputChanged(helper, visible));
        }
    }

    /**
     *
     */
    private class ComposeScrollListener extends RecyclerView.OnScrollListener {

        int scrollState = RecyclerView.SCROLL_STATE_IDLE;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if ((scrollState != RecyclerView.SCROLL_STATE_IDLE)
                    && (newState == RecyclerView.SCROLL_STATE_IDLE)) {

            }

            this.scrollState = newState;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /**
     *
     */
    private class ComposeTouchHelper extends EditTouchHelper {

        @Override
        protected TextView findTextView(View child) {
            RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(child);

            // only handle touch for ParagraphViewHolder
            if ((h != null) && (!(h instanceof ParagraphViewHolder))) {
                return null;
            }

            return super.findTextView(child);
        }
    }

    /**
     *
     */
    private static class ViewEntityHelper {

        HashMap<Class<? extends ComposeViewHolder>, Consumer<ComposeViewHolder>> map;

        ComposeArticleFragment parent;

        ViewEntityHelper(ComposeArticleFragment f) {
            this.parent = f;

            this.map = new HashMap<>();

            map.put(PictureViewHolder.class, (holder) -> {

                GalleryDelegate delegate = new GalleryDelegate(parent, parent.composeHelper, parent.document.getId(), holder.getId());
                parent.requestResultManager.request(delegate);

            });

            map.put(FormulaViewHolder.class, (holder) -> {

                FormulaDelegate delegate = new FormulaDelegate(parent, (FormulaEntity) (holder.getEntity()), parent.insertionCallback);
                parent.requestResultManager.request(delegate);


            });
        }

        void request(final ComposeViewHolder viewHolder) {
            map.computeIfPresent(viewHolder.getClass(), (kind, action) -> {
                action.accept(viewHolder);
                return action;
            });
        }
    }

    /**
     *
     */
    private static class ComposeEntityHelper {

        HashMap<Class<? extends ComposeViewHolder>, Consumer<ComposeViewHolder>> map;

        ComposeArticleFragment parent;

        ComposeEntityHelper(ComposeArticleFragment f) {
            this.parent = f;

            this.map = new HashMap<>();

            map.put(FormulaViewHolder.class, (holder) -> {
                FormulaDelegate delegate = new FormulaDelegate(parent, (FormulaEntity) (holder.getEntity()), parent.insertionCallback);
                parent.requestResultManager.request(delegate);
            });
        }

        void request(final ComposeViewHolder viewHolder) {
            map.computeIfPresent(viewHolder.getClass(), (kind, action) -> {
                action.accept(viewHolder);
                return action;
            });
        }
    }

    /**
     *
     */
    private static class ComposeMenuItemListener extends MenuItemClickListener {

        ComposeArticleFragment parent;

        ComposeMenuItemListener(ComposeArticleFragment f) {
            this.parent = f;

            this.put(R.id.menu_insert, e -> parent.requestInsertion());

            this.put(R.id.menu_more, e -> parent.requestMore());
        }

    }

    /**
     *
     */
    private static class ComposeCallback extends ComposeViewHolder.Callback {

        ComposeArticleFragment parent;

        ComposeCallback(ComposeArticleFragment f) {
            super(f.getActivity());

            this.parent = f;
        }

        @Override
        public void remove(ComposeViewHolder holder) {
            parent.deletionHelper.execute(holder);
        }

        @Override
        public void view(ComposeViewHolder viewHolder) {
            parent.viewEntityHelper.request(viewHolder);
        }

        @Override
        public void compose(ComposeViewHolder viewHolder) {
            parent.composeEntityHelper.request(viewHolder);
        }

        @Override
        public int getMaxWidth() {
            int width = parent.recyclerView.getWidth();
            if (width > 0) {
                width -= parent.recyclerView.getPaddingLeft();
                width -= parent.recyclerView.getPaddingRight();
            }

            if (width <= 0) {
                width = super.getMaxWidth();
            }

            return width;
        }

        @Override
        public int getMaxHeight() {
            int width = parent.recyclerView.getHeight();

            if (width <= 0) {
                width = super.getMaxHeight();
            }

            return width;
        }

        @Override
        public void afterTextChanged(ComposeViewHolder viewHolder, View view) {
            parent.undoHelper.updateButtons();
        }

        @Override
        public void requestSave(ComposeViewHolder viewHolder) {
            parent.saveHelper.saveDocumentOnly();
        }
    }
}
