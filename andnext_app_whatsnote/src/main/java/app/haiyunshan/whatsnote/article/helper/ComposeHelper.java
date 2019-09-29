package app.haiyunshan.whatsnote.article.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import app.haiyunshan.whatsnote.article.viewholder.ComposeViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;
import app.haiyunshan.whatsnote.record.entity.RecentEntity;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.SavedStateEntity;
import club.andnext.helper.SoftInputHelper;
import club.andnext.snapshot.InstantShot;
import club.andnext.utils.SoftInputUtils;

import java.util.Optional;
import java.util.stream.IntStream;

public class ComposeHelper {

    RecentEntity recentEntity;
    RecordEntity recordEntity;
    SavedStateEntity savedStateEntity;

    InstantShot instantShot;

    Fragment parent;
    Document document;
    RecyclerView recyclerView;
    SoftInputHelper softInputHelper;

    Activity context;

    public ComposeHelper(Fragment f, Document document, RecyclerView recyclerView, SoftInputHelper softInputHelper) {
        this.context = f.getActivity();

        this.parent = f;
        this.document = document;
        this.recyclerView = recyclerView;
        this.softInputHelper = softInputHelper;

        Optional<RecordEntity> entity = RecordEntity.create(document.getId());

        this.recordEntity = entity.get();
        this.recentEntity = RecentEntity.create(document.getId());
        this.savedStateEntity = SavedStateEntity.create(document.getId());
    }

    public void recycle() {

        if (instantShot != null) {
            instantShot.recycle();
            instantShot = null;
        }

    }

    public RecordEntity getEntity() {
        return recordEntity;
    }

    public Document getDocument() {
        return document;
    }

    public boolean showSoftInput() {
        boolean showSoftInput = false;

        if (document.size() == 1) {
            DocumentEntity e = document.get(0);
            if (e.getClass() == ParagraphEntity.class) {
                ParagraphEntity entity = (ParagraphEntity) e;
                if (entity.getText().length() == 0) {
                    showSoftInput = true;
                }
            }
        }

        if (showSoftInput) {
            recyclerView.post(() -> {
                EditText edit = findEditText(recyclerView);
                if (edit != null) {
                    SoftInputUtils.show(context, edit);
                }

            });
        }

        return showSoftInput;
    }

    InstantShot getInstantShot() {
        if (instantShot != null) {
            return instantShot;
        }

        Rect rect = new Rect();
        recyclerView.getGlobalVisibleRect(rect);

        SoftInputHelper helper = softInputHelper;
        int height = helper.getBottom() - rect.top;
        int width = rect.width();

        float scale = 0.2f;
        width = (int)(width * scale);
        height = (int)(height * scale);

        instantShot = new InstantShot(recyclerView, width, height);

        return instantShot;
    }

    EditText findEditText(View view) {
        if (view instanceof EditText) {
            return (EditText)view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup)view;
            for (int i = 0, size = layout.getChildCount(); i < size; i++) {
                EditText edit = findEditText(layout.getChildAt(i));
                if (edit != null) {
                    return edit;
                }
            }
        }

        return null;
    }

    ComposeViewHolder getViewHolder(int index) {
        View child = recyclerView.getChildAt(index);
        RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(child);
        if (h != null && h instanceof ComposeViewHolder) {
            return (ComposeViewHolder)h;
        }

        return null;
    }

    <T> T findViewHolder(final String id, Class<? extends ComposeViewHolder> kind) {
        ComposeViewHolder holder = IntStream.range(0, recyclerView.getChildCount())
                .mapToObj(i -> recyclerView.getChildAt(i))
                .map(v -> recyclerView.findContainingViewHolder(v))
                .map(h -> (ComposeViewHolder)h)
                .filter(h -> h.getEntity().getId().equals(id))
                .filter(h -> (kind.isAssignableFrom(h.getClass())))
                .findFirst().orElse(null);

        return (T)holder;
    }

    ParagraphViewHolder findParagraphViewHolder(final String id) {
        return IntStream.range(0, recyclerView.getChildCount())
                .mapToObj(i -> recyclerView.getChildAt(i))
                .map(v -> recyclerView.findContainingViewHolder(v))
                .filter(h -> (h instanceof ParagraphViewHolder))
                .map(h -> (ParagraphViewHolder)h)
                .filter(h -> h.getEntity().getId().equals(id))
                .findAny().orElse(null);

    }

    public final void scrollTo(final String id) {
        int index = document.indexOf(id);
        if (index < 0) {
            return;
        }

        final int position = index;
        recyclerView.post(() -> {

            View child = null;
            for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
                if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i)) == position) {
                    child = recyclerView.getChildAt(i);
                    break;
                }
            }

            if (child == null) {
                recyclerView.scrollToPosition(position);
            }

        });
    }
}
