package app.haiyunshan.whatsnote.outline.entity;

import android.text.TextUtils;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.article.entity.FormulaEntity;
import club.andnext.xsltml.MathMLTransformer;

import javax.xml.transform.TransformerException;

public class FormulaOutlineEntity extends BaseOutlineEntity<FormulaEntity> {

    String latex;

    public FormulaOutlineEntity(FormulaEntity parent) {
        super(parent);
    }

    public String getLatex() {
        if (!TextUtils.isEmpty(latex)) {
            return latex;
        }

        // try latex
        latex = parent.getLatex();
        if (!TextUtils.isEmpty(latex)) {
            return latex;
        }

        // try formula
        latex = parent.getFormula();

        String formula = latex;
        if (MathMLTransformer.isMathML(formula)) {

            if (MathMLTransformer.isMathML(formula)) {
                MathMLTransformer transformer = WhatsApp.getInstance().getMathMLTransformer();
                try {
                    formula = transformer.transform(formula);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }

            if (formula == null) {
                formula = parent.getFormula();
            }

            latex = formula;
        }

        return latex;
    }

}
