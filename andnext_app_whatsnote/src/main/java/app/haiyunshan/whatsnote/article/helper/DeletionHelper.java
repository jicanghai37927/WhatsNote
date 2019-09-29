package app.haiyunshan.whatsnote.article.helper;

import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.article.delegate.FormulaDelegate;
import app.haiyunshan.whatsnote.article.deletion.BaseDeletion;
import app.haiyunshan.whatsnote.article.deletion.FormulaDeletion;
import app.haiyunshan.whatsnote.article.deletion.ParagraphDeletion;
import app.haiyunshan.whatsnote.article.deletion.PictureDeletion;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import app.haiyunshan.whatsnote.article.viewholder.ComposeViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.FormulaViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.ParagraphViewHolder;
import app.haiyunshan.whatsnote.article.viewholder.PictureViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class DeletionHelper {

    BaseDeletion.Callback callback;

    HashMap<Class<? extends ComposeViewHolder>, Class<? extends BaseDeletion>> map;

    public DeletionHelper(BaseDeletion.Callback callback) {

        this.callback = callback;

        this.map = new HashMap<>();
        map.put(ParagraphViewHolder.class, ParagraphDeletion.class);
        map.put(PictureViewHolder.class, PictureDeletion.class);
        map.put(FormulaViewHolder.class, FormulaDeletion.class);
    }

    public BaseDeletion execute(ComposeViewHolder holder) {
        BaseDeletion action = getAction(holder);
        if (action != null) {
            action.execute();
        }

        return action;
    }

    BaseDeletion getAction(ComposeViewHolder holder) {
        Document document = callback.getDocument();
        RecyclerView.Adapter adapter = callback.getRecyclerView().getAdapter();

        DocumentEntity entity = holder.getEntity();

        if (document.indexOf(entity) <= 0) {
            return null;
        }

        Class<? extends BaseDeletion> clz = map.get(holder.getClass());
        if (clz == null) {

            int index = document.remove(entity);
            if (index >= 0) {
                adapter.notifyItemRemoved(index);
            }

            if (index >= 0) {
                SaveHelper.save(document);
            }

        } else {

            try {
                Constructor<? extends BaseDeletion> c = clz.getConstructor(BaseDeletion.Callback.class, holder.getClass());
                BaseDeletion obj = c.newInstance(callback, holder);

                return obj;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
