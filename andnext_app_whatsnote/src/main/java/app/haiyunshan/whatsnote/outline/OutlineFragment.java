package app.haiyunshan.whatsnote.outline;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.helper.SaveHelper;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.outline.entity.*;
import app.haiyunshan.whatsnote.outline.helper.OutlineFactory;
import app.haiyunshan.whatsnote.outline.helper.OutlineHelper;
import app.haiyunshan.whatsnote.outline.viewholder.BaseOutlineViewHolder;
import app.haiyunshan.whatsnote.outline.viewholder.FormulaOutlineViewHolder;
import app.haiyunshan.whatsnote.outline.viewholder.ParagraphOutlineViewHolder;
import app.haiyunshan.whatsnote.outline.viewholder.PictureOutlineViewHolder;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.dialog.PopupMenuDialogFragment;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.decoration.RemoveDecoration;
import club.andnext.recyclerview.itemtouch.SimpleDragCallback;
import club.andnext.recyclerview.swipe.SwipeActionHelper;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutlineFragment extends Fragment {

    TitleBar titleBar;

    TextView indentBtn;

    RecyclerView recyclerView;
    BridgeAdapter adapter;

    MarginDividerDecoration dividerDecoration;
    RemoveDecoration removeDecoration;

    ItemTouchHelper itemTouchHelper;
    ItemDragCallback itemDragCallback;

    SwipeActionHelper swipeActionHelper;

    OutlineHelper outlineHelper;
    OutlineFactory outlineFactory;

    Outline outline;
    Document result;

    public OutlineFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.outlineFactory = new OutlineFactory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            view.setClipToOutline(true);

            int flags = View.SYSTEM_UI_FLAG_VISIBLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            view.setSystemUiVisibility(flags);
        }

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            titleBar.setTitle("选择项目");
            titleBar.setNegativeButton("全选", this::onNegativeClick);
            titleBar.setPositiveButton("完成", this::onDoneClick);
        }

        {
            this.indentBtn = view.findViewById(R.id.btn_indent);
            indentBtn.setOnClickListener(this::onIndentClick);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);

            recyclerView.addOnScrollListener(new OutlineScrollListener());
        }

        {
            this.removeDecoration = new RemoveDecoration(getActivity());
            recyclerView.addItemDecoration(removeDecoration);
        }

        {
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setDrawOver(false);
            dividerDecoration.setDrawTop(false);
            dividerDecoration.setLastMargin(false);
            dividerDecoration.setMargin(getResources().getDimensionPixelSize(R.dimen.entrance_item_padding));
//            recyclerView.addItemDecoration(dividerDecoration);
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

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = args.getString("id");

            Document document = Document.create(id);
            this.outline = outlineFactory.from(document);
        }

        {

            this.outlineHelper = new OutlineHelper(new OutlineCallback(this));

            this.adapter = new BridgeAdapter(getActivity(), new OutlineProvider());

            adapter.bind(ParagraphOutlineEntity.class,
                    new BridgeBuilder(ParagraphOutlineViewHolder.class, ParagraphOutlineViewHolder.LAYOUT_RES_ID, outlineHelper));

            adapter.bind(PictureOutlineEntity.class,
                    new BridgeBuilder(PictureOutlineViewHolder.class, PictureOutlineViewHolder.LAYOUT_RES_ID, outlineHelper));

            adapter.bind(FormulaOutlineEntity.class,
                    new BridgeBuilder(FormulaOutlineViewHolder.class, FormulaOutlineViewHolder.LAYOUT_RES_ID, outlineHelper));
        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    void onNegativeClick(View view) {
        int count = outlineHelper.getCheckedCount();
        boolean all = (count == outline.size());
        all = !all;

        outlineHelper.setChecked(all);
        adapter.notifyDataSetChanged();
    }

    void onDoneClick(View view) {
        getActivity().onBackPressed();
    }

    void onIndentClick(View view) {
        outlineHelper.setExpand(null);

        swipeActionHelper.clear();

        requestIndent();
    }

    void requestIndent() {

        {
            this.scaleContent(true);
        }

        DialogInterface.OnCancelListener onCancelListener = (dialog) -> scaleContent(false);
        BiConsumer<Integer, String> listener = new IndentListener();

        {
            IndentDialogFragment dialogFragment = new IndentDialogFragment();
            dialogFragment.setTheme(R.style.DialogDimTheme);
            dialogFragment.setOnCancelListener(onCancelListener);
            dialogFragment.setOnIndentListener(listener);

            dialogFragment.showNow(getFragmentManager(), "indent");
        }
    }

    void requestDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
        dividerDecoration.setDragViewHolder(viewHolder);
    }

    void requestRemove(BaseOutlineViewHolder viewHolder, boolean showConfirm) {

        final BaseOutlineEntity entity = viewHolder.getItem();
        final int index = outline.indexOf(entity);
        if (index < 0) {
            return;
        }

        {
            {
                outlineHelper.setExpand(null);
            }

            // hide children
            viewHolder.hide();

            //
            this.addRemove(viewHolder);

            outline.remove(entity);

            adapter.notifyItemRemoved(index);
        }

        MenuItemClickListener listener = new MenuItemClickListener();
        {
            listener.put(R.id.menu_delete, (item -> {
                {
                    entity.delete();
                    outlineHelper.setChecked(entity, false);
                }

                {
                    int size = outline.size();
                    titleBar.setNegativeEnable(size > 0);
                    indentBtn.setEnabled(size > 0);
                }

                // save document
                {
                    save();
                }
            }));
        }

        if (!showConfirm) {
            listener.onMenuItemClick(R.id.menu_delete);
        } else {
            DialogInterface.OnCancelListener onCancelListener = (dialog) -> {
                outline.add(index, entity);
                adapter.notifyItemInserted(index);

                if (index + 1 == outline.size()) {
                    recyclerView.smoothScrollToPosition(index);
                }
            };

            {
                PopupMenuDialogFragment dialogFragment = new PopupMenuDialogFragment();
                dialogFragment.setTheme(R.style.DialogDimTheme);
                dialogFragment.setMenuResource(R.menu.menu_outline_delete);
                dialogFragment.setButtonAppearance(R.style.OutlineDeleteButton);
                dialogFragment.setOnMenuItemClickListener(listener);
                dialogFragment.setOnCancelListener(onCancelListener);

                dialogFragment.showNow(getFragmentManager(), "delete");
            }
        }
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

    void scaleContent(boolean value) {

        View view = this.getView();
        Drawable bg = view.getBackground();
        ViewPropertyAnimator animator = view.animate();

        {
            long duration = (value)? getResources().getInteger(android.R.integer.config_shortAnimTime) : 160;
            animator.setDuration(duration);
        }

        {
            float scale = value? 0.92f: 1.f;

            animator.cancel();
            animator.scaleX(scale).scaleY(scale).start();
        }

        if (bg instanceof GradientDrawable) {

            float radius = value? getResources().getDisplayMetrics().widthPixels * 0.042f: 0.f;

            GradientDrawable d = (GradientDrawable)bg;
            ObjectAnimator.ofFloat(d, "CornerRadius", radius).setDuration(animator.getDuration()).start();

        }
    }

    void save() {
        Document document = outlineFactory.from(outline);
        SaveHelper.save(document);

        if (result == null) {
            Intent intent = new Intent();
            intent.putExtra("id", outline.getDocument().getId());

            Activity context = this.getActivity();
            context.setResult(Activity.RESULT_OK, intent);
        }

        this.result = document;
    }

    /**
     *
     */
    private class OutlineProvider implements BridgeAdapterProvider<BaseOutlineEntity> {

        @Override
        public BaseOutlineEntity get(int position) {
            return outline.get(position);
        }

        @Override
        public int size() {
            return outline.size();
        }
    }

    /**
     *
     */
    private class SwipeActionListener implements SwipeActionHelper.OnSwipeActionListener {

        @Override
        public void onSwipeChanged(SwipeActionHelper helper, boolean oldValue, boolean newValue) {

        }

        @Override
        public void onExpandChanged(SwipeActionHelper helper, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder == null) {
                outlineHelper.setExpand(null);
            } else {
                BaseOutlineViewHolder holder = (BaseOutlineViewHolder)viewHolder;
                outlineHelper.setExpand(holder.getItem());
            }
        }
    }

    /**
     *
     */
    private class OutlineScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (outlineHelper != null) {
                outlineHelper.setExpand(null);
            }
        }
    }

    /**
     *
     */
    private class IndentListener implements BiConsumer<Integer, String> {

        @Override
        public void accept(Integer count, String type) {
            scaleContent(false);

            this.indent(count, type);

            // save document
            save();
        }

        void indent(int count, String type) {
            char c = (type.equals(IndentDialogFragment.TYPE_CHINESE))? '\u3000': ' ';
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(c);
            }

            List<BaseOutlineEntity> list = outline.getList();
            if (outlineHelper.getCheckedCount() != 0) {
                list = outlineHelper.getCheckedList();
            }

            // indent
            {
                for (BaseOutlineEntity entity : list) {
                    entity.indent(sb);
                }
            }

            // notify RecyclerView changed
            {
                adapter.notifyDataSetChanged();
            }

        }
    }

    /**
     *
     */
    private static class ItemDragCallback extends SimpleDragCallback {

        BiConsumer<RecyclerView.ViewHolder, RecyclerView.ViewHolder> dragConsumer;

        OutlineFragment parent;

        boolean isMoved = false;

        public ItemDragCallback(OutlineFragment f) {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);

            this.elevation = 12.f;

            this.parent = f;

            this.dragConsumer = (viewHolder, target) -> {

                Object from = ((BaseOutlineViewHolder)viewHolder).getItem();
                Object to = ((BaseOutlineViewHolder)target).getItem();

                parent.outline.move(from, to);

            };
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            {
                BiConsumer consumer = this.dragConsumer;
                if (consumer != null) {
                    consumer.accept(viewHolder, target);
                }
            }

            {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                recyclerView.getAdapter().notifyItemMoved(from, to);
            }

            {
                this.isMoved = true;
            }

            return true;
        }

        @Override
        public boolean canDropOver(RecyclerView recyclerView,
                                   RecyclerView.ViewHolder current,
                                   RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            parent.dividerDecoration.setDragViewHolder(null);

            // save document
            if (isMoved) {
                parent.save();
            }
        }
    }

    /**
     *
     */
    private static class OutlineCallback implements OutlineHelper.Callback {

        OutlineFragment parent;

        OutlineCallback(OutlineFragment f) {
            this.parent = f;
        }

        @Override
        public Outline getOutline() {
            return parent.outline;
        }

        @Override
        public SwipeActionHelper getSwipeHelper() {
            return parent.swipeActionHelper;
        }

        @Override
        public void requestDrag(RecyclerView.ViewHolder viewHolder) {
            parent.requestDrag(viewHolder);
        }

        @Override
        public void requestRemove(BaseOutlineViewHolder viewHolder) {
            parent.requestRemove(viewHolder, false);
        }

        @Override
        public void onCheckedChanged(OutlineHelper helper) {
            int count = helper.getCheckedCount();
            int size = getOutline().size();

            String title = (count == 0)? "选择项目": String.format("%1$d 项", count);
            String btn = ((size != 0) && (count == size))? "取消全选": "全选";
            String indent = (count == 0)? "全部缩进": "缩进";

            parent.titleBar.setTitle(title);
            parent.titleBar.setNegativeText(btn);
            parent.indentBtn.setText(indent);
        }
    }
}
