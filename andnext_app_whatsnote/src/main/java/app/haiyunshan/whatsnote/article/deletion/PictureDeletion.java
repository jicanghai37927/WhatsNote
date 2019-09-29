package app.haiyunshan.whatsnote.article.deletion;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import app.haiyunshan.whatsnote.article.helper.SaveHelper;
import app.haiyunshan.whatsnote.article.viewholder.PictureViewHolder;
import app.haiyunshan.whatsnote.record.entity.ImageEntity;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class PictureDeletion extends BaseDeletion<PictureViewHolder> {

    @Keep
    public PictureDeletion(Callback f, PictureViewHolder holder) {
        super(f, holder);
    }

    @Override
    public void execute() {

        Document document = callback.getDocument();
        RecyclerView recyclerView = callback.getRecyclerView();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        DocumentEntity entity = holder.getEntity();

        int index = document.indexOf(entity);
        if (index >= 0) { // remove picture file
            this.removeFile(holder.getEntity().getFile());
        }

        if (index >= 0) { // remove image entry
            ImageEntity r = ImageEntity.obtain(holder.getEntity().getId(), document.getId()).delete();
            r.save();
        }

        if (index >= 0) {
            int position = index;
            int count = 1;

            // next one
            index = position + 1;
            if (index < document.size()) {
                DocumentEntity e = document.get(index);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity en = (ParagraphEntity)e;
                    if (en.getText().length() == 0) {
                        count += 1;
                    }
                }
            }

            // if the last paragraph did't removed, we try the previous one
            index = position - 1;
            if ((count == 1) && (index > 0)) { // cannot remove first paragraph
                DocumentEntity e = document.get(index);
                if (e.getClass() == ParagraphEntity.class) {
                    ParagraphEntity en = (ParagraphEntity)e;
                    if (en.getText().length() == 0) {
                        count += 1;

                        position -= 1;
                    }
                }
            }

            // remove them
            for (int i = 0; i < count; i++) {
                document.remove(position);
            }

            // notify changed
            adapter.notifyItemRangeRemoved(position, count);
        }

        if (index >= 0) {
            SaveHelper.save(document);
        }
    }

    void removeFile(File file) {
        if (!file.exists()) {
            return;
        }

        CompletableFuture.runAsync(() -> file.delete());
    }
}
