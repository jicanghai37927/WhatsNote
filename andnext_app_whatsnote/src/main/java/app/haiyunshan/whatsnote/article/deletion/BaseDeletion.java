package app.haiyunshan.whatsnote.article.deletion;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.viewholder.ComposeViewHolder;

public class BaseDeletion<T extends ComposeViewHolder> {

    T holder;

    Callback callback;

    BaseDeletion(Callback cb, T holder) {
        this.callback = cb;

        this.holder = holder;

    }

    public void execute() {

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