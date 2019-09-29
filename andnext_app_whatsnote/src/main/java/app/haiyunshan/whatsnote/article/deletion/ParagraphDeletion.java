package app.haiyunshan.whatsnote.article.deletion;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import app.haiyunshan.whatsnote.article.helper.SaveHelper;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;

public class ParagraphDeletion extends BaseDeletion<ParagraphViewHolder> {

    @Keep
    public ParagraphDeletion(BaseDeletion.Callback callback, ParagraphViewHolder holder) {
        super(callback, holder);
    }

    @Override
    public void execute() {

        Document document = callback.getDocument();
        RecyclerView recyclerView = callback.getRecyclerView();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        DocumentEntity entity = holder.getEntity();
        if (document.size() == 1 || document.indexOf(entity) == 0) {
            return;
        }

        ParagraphViewHolder previous = null;
        int index = recyclerView.indexOfChild(holder.itemView);
        if (index > 0) {
            RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(recyclerView.getChildAt(index - 1));
            if (h instanceof ParagraphViewHolder) {
                previous = (ParagraphViewHolder)h;
            }
        }

        if (previous != null) {
            index = document.remove(entity);
            if (index >= 0) {
                int position = previous.length();

                CharSequence s = holder.getText();
                if (s.length() != 0) {
                    s = previous.getText().append(s);
                    previous.setText(s);
                }

                previous.setSelection(position);
                previous.requestFocus();
            }

            if (index >= 0) {
                adapter.notifyItemRemoved(index);
            }

            if (index >= 0) {
                SaveHelper.save(document);
            }
        }

    }
}
