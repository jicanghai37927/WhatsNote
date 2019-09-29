package app.haiyunshan.whatsnote.article.insertion;

import android.app.Activity;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import app.haiyunshan.whatsnote.article.helper.SaveHelper;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;

import java.io.File;

/**
 *
 */
public abstract class BaseInsertion {

    boolean alwaysSplit;

    Callback callback;

    BaseInsertion(Callback cb) {
        this.callback = cb;

        this.alwaysSplit = false;
    }

    abstract ParagraphEntity insert(int position, CharSequence text);

    final Activity getActivity() {
        return callback.getActivity();
    }

    final ParagraphViewHolder getFocus() {
        RecyclerView recyclerView = callback.getRecyclerView();
        ParagraphViewHolder holder = null;

        {
            View focus = getActivity().getCurrentFocus();
            if (focus != null) {
                RecyclerView.ViewHolder h = recyclerView.findContainingViewHolder(focus);
                if (h instanceof ParagraphViewHolder) {
                    holder = (ParagraphViewHolder) h;
                }
            }
        }

        return holder;
    }

    public void execute() {

        Document document = callback.getDocument();
        RecyclerView recyclerView = callback.getRecyclerView();

        ParagraphEntity result = null;

        ParagraphViewHolder focus = getFocus();
        ParagraphEntity current = (focus != null)? focus.getEntity(): null;

        // we may split Paragraph text and inert photos
        if (focus != null) {

            if (!alwaysSplit) {

                ParagraphEntity entity = focus.getEntity();
                int index = document.indexOf(entity);
                boolean isPreviousParagraph = (index == 0) ? false : (document.get(index - 1).getClass() == ParagraphEntity.class);

                if (focus.length() == 0) {

                    if (isPreviousParagraph) {
                        result = insert(index, null);
                        if (result == null) {
                            result = entity;
                        }
                    } else {
                        result = insert(index + 1, "");
                    }

                } else {

                    int start = focus.getSelectionStart();
                    int end = focus.getSelectionEnd();
                    if (start == end) {

                        int position = start;
                        if (position == 0) {
                            if (isPreviousParagraph) {
                                result = insert(index, null);
                                if (result == null) {
                                    result = entity;
                                }
                            }
                        } else if (position == focus.length()) {
                            CharSequence text = "";
                            ParagraphEntity next = entity;

                            if (index + 1 < document.size()) {
                                boolean isNextParagraph = (document.get(index + 1).getClass() == ParagraphEntity.class);

                                text = isNextParagraph? null: text;
                                next = isNextParagraph? (ParagraphEntity) (document.get(index + 1)): next;
                            }

                            {
                                result = insert(index + 1, text);
                                if (result == null) {
                                    result = next;
                                }
                            }
                        }
                    }
                }
            }

            if (result == null) {
                CharSequence[] array = focus.split();
                if (array != null && array.length == 2) {
                    int position = document.indexOf(focus.getEntity());
                    if (position >= 0) {
                        result = insert(position + 1, array[1]);
                        if (result != null) {
                            focus.setText(array[0]);
                            focus.setSelection(focus.length());
                        }
                    }
                }
            }
        }

        // if current focus is not Paragraph, we just append at last
        if (focus == null) {
            result = insert(document.size(), "");
        }

        // move to new position
        if ((result != null) && (result != current)) {
            final int index = document.indexOf(result);
            if (index >= 0) {
                recyclerView.post(() -> {

                    int position = index;
                    recyclerView.smoothScrollToPosition(position);

                    RecyclerView.ViewHolder h = recyclerView.findViewHolderForAdapterPosition(position);
                    if (h != null && h instanceof ParagraphViewHolder) {
                        ParagraphViewHolder holder = (ParagraphViewHolder)h;
                        holder.setSelection(0);
                        holder.requestFocus();
                    }

                });
            }
        }

        // save document
        if (result != null) {
            SaveHelper.save(document);
        }
    }

    RecyclerView.Adapter getAdapter() {
        return callback.getRecyclerView().getAdapter();
    }

    public static File getTempFile(File file) {
        return new File(file.getAbsolutePath() + ".tmp");
    }

    /**
     *
     */
    public static abstract class Callback {

        public abstract Activity getActivity();

        public abstract RecyclerView getRecyclerView();

        public abstract Document getDocument();

    }
}
