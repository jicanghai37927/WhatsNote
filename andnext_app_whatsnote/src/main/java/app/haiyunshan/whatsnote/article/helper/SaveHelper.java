package app.haiyunshan.whatsnote.article.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import app.haiyunshan.whatsnote.article.viewholder.ComposeViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;
import app.haiyunshan.whatsnote.record.entity.RecentEntity;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.SavedStateEntity;
import club.andnext.snapshot.InstantShot;
import club.andnext.utils.BitmapUtils;
import club.andnext.utils.FileUtils;
import org.joda.time.DateTime;

import java.io.File;

public class SaveHelper {

    RecentEntity recentEntity;
    RecordEntity recordEntity;
    SavedStateEntity savedStateEntity;

    Document document;
    RecyclerView recyclerView;

    Activity context;

    ComposeHelper helper;

    public SaveHelper(ComposeHelper helper) {
        this.recentEntity = helper.recentEntity;
        this.recordEntity = helper.recordEntity;
        this.savedStateEntity = helper.savedStateEntity;

        this.document = helper.document;
        this.recyclerView = helper.recyclerView;
        this.context = helper.context;

        this.helper = helper;
    }

    public static final void save(Document document) {
        document.cache();
        document.save();
    }

    public void restore() {
        final SavedStateEntity params = this.savedStateEntity;

        {
            final String id = params.getOffsetId();
            int index = (!TextUtils.isEmpty(id)) ? (document.indexOf(id)) : -1;
            if (index > 0) {
                recyclerView.scrollToPosition(index);
            }
        }

        recyclerView.post(() -> {

            {
                String id = params.getOffsetId();

                if (!TextUtils.isEmpty(id)) {
                    int vertical = params.getOffsetTop();
                    int textOffset = params.getTextOffset();

                    int y = vertical;

                    if (textOffset > 0) {

                        ParagraphViewHolder holder = helper.findParagraphViewHolder(id);
                        if (holder != null) {
                            y = holder.getVertical(textOffset);
                        }
                    }

                    // calculate current scroll
                    {
                        ComposeViewHolder holder = helper.findViewHolder(id, ComposeViewHolder.class);
                        if (holder != null) {
                            y += holder.itemView.getTop();
                        }
                    }

                    recyclerView.scrollBy(0, y);
                }

            }

            boolean enable = false;
            if (enable) {
                String id = params.getFocusId();
                int start = params.getSelectionStart();
                int end = params.getSelectionEnd();
                if (!(TextUtils.isEmpty(id)) && start >= 0 && end >= 0) {
                    ParagraphViewHolder holder = helper.findParagraphViewHolder(id);
                    if (holder != null) {
                        holder.setSelection(start, end);
                        holder.requestFocus();
                    }
                }
            }
        });


    }

    public void save() {
        this.saveSnapshot();

        this.saveDocument();

        this.saveRecent();
        this.saveRecord();
        this.saveState();
    }

    public void saveDocumentOnly() {
        this.saveDocument();
    }

    public void clearSavedState() {
        this.savedStateEntity.clear();

        savedStateEntity.save();
    }

    void saveDocument() {

        if (document.isReadOnly()) {
            return;
        }

        int count = recyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            ComposeViewHolder holder = helper.getViewHolder(i);
            if (holder != null) {
                holder.save();
            }
        }

        SaveHelper.save(document);
    }

    void saveRecent() {

        if (document.isReadOnly()) {
            return;
        }

        if (recentEntity != null) {
            recentEntity.save();
            recentEntity = null;
        }
    }

    void saveRecord() {

        if (document.isReadOnly()) {
            return;
        }

        File file = document.getFile();
        long size = FileUtils.sizeOf(file);

        recordEntity.setSize(size);
        recordEntity.setModified(DateTime.now());

        if (recordEntity.isAlias()) {
            String name = document.getTitle();
            if (!TextUtils.isEmpty(name)) {
                name = recordEntity.getAlias(name);
                recordEntity.setAlias(name);
            }
        }

        recordEntity.save();
    }

    void saveSnapshot() {

        if (document.isReadOnly()) {
            return;
        }

        InstantShot instantShot = helper.getInstantShot();
        instantShot.update();

        File file = recordEntity.getSnapshot();
        BitmapUtils.writeBitmapToFile(instantShot.getBitmap(), Bitmap.CompressFormat.JPEG, 50, file);

    }

    void saveState() {

        if (document.isReadOnly()) {
            return;
        }

        if (recyclerView.getChildCount() == 0) {
            savedStateEntity.clear();

            return;
        }

        // offset
        {
            View child = recyclerView.getChildAt(0);
            ComposeViewHolder h = (ComposeViewHolder) (recyclerView.findContainingViewHolder(child));
            DocumentEntity entity = h.getEntity();
            int index = document.indexOf(entity);

            String id = entity.getId();
            int top = child.getTop();
            int textOffset = 0;

            if (h instanceof ParagraphViewHolder) {
                ParagraphViewHolder holder = (ParagraphViewHolder) h;
                textOffset = holder.getOffset();
            }

            if (index == 0 && top >= 0) {
                id = null;
                top = 0;
                textOffset = 0;
            }

            Log.w("AA", "id = " + id + ", top = " + top + ", offset = " + textOffset);
            savedStateEntity.setOffset(id, Math.abs(top), textOffset);
        }

        // focus
        {
            View view = context.getCurrentFocus();
            if (view != null) {

                String id = null;
                int start = -1;
                int end = -1;

                RecyclerView.ViewHolder h = (recyclerView.findContainingViewHolder(view));
                if (h != null && h instanceof ParagraphViewHolder) {
                    ParagraphViewHolder holder = (ParagraphViewHolder) h;

                    id = (holder.getEntity().getId());
                    start = ((holder.getSelectionStart()));
                    end = ((holder.getSelectionEnd()));
                }

                savedStateEntity.setFocus(id, start, end);
            }
        }

        {
            savedStateEntity.save();
        }
    }
}
