package app.haiyunshan.whatsnote.article.insertion;

import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;

/**
 *
 */
public class ParagraphInsertion extends BaseInsertion {

    public ParagraphInsertion(Callback cb) {
        super(cb);

        this.alwaysSplit = true;
    }

    @Override
    ParagraphEntity insert(int position, CharSequence text) {
        Document document = callback.getDocument();

        ParagraphEntity entity;

        int index = position;
        {
            CharSequence s = (text == null)? "": text;
            entity = ParagraphEntity.create(document, s);

            document.add(index, entity);
            ++index;
        }

        int count = (index - position);
        if (count > 0) {
            getAdapter().notifyItemRangeInserted(position, count);
        }

        return entity;
    }
}
