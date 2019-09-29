package app.haiyunshan.whatsnote.article.insertion;

import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.FormulaEntity;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;

/**
 *
 */
public class FormulaInsertion extends BaseInsertion {

    String formula;
    String latex;

    public FormulaInsertion(Callback callback, String formula, String latex) {
        super(callback);

        this.formula = formula;
        this.latex = latex;
    }

    @Override
    ParagraphEntity insert(int position, CharSequence text) {
        Document document = callback.getDocument();

        ParagraphEntity entity = null;

        int index = position;
        for (int i = 0, size = 1; i < size; i++) {

            FormulaEntity pic = FormulaEntity.create(document, formula, latex);

            if (pic != null) { // add target
                document.add(index, pic);
                ++index;

                CharSequence s = ((i + 1) == size)? text: "";
                if (s != null) {
                    ParagraphEntity en = ParagraphEntity.create(document, s);

                    document.add(index, en);
                    ++index;

                    entity = en;
                }
            }
        }

        int count = (index - position);
        if (count > 0) {
            getAdapter().notifyItemRangeInserted(position, count);
        }

        return entity;
    }
}

