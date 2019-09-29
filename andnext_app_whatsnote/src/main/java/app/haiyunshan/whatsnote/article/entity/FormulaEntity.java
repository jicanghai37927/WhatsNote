package app.haiyunshan.whatsnote.article.entity;

import app.haiyunshan.whatsnote.article.entry.FormulaEntry;
import club.andnext.utils.UUIDUtils;

public class FormulaEntity extends ParagraphEntity<FormulaEntry> {

    public FormulaEntity(Document d, FormulaEntry entry) {
        super(d, entry);
    }

    @Override
    protected void save() {
        super.save();
    }

    public String getFormula() {
        return entry.getFormula();
    }

    public void setFormula(String formula) {
        entry.setFormula(formula);
    }

    public String getLatex() {
        return entry.getLatex();
    }

    public void setLatex(String latex) {
        entry.setLatex(latex);
    }

    public static final FormulaEntity create(Document d, String formula, String latex) {

        String id = UUIDUtils.next();
        FormulaEntry entry = new FormulaEntry(id, formula, latex);

        FormulaEntity entity = new FormulaEntity(d, entry);

        return entity;
    }
}
