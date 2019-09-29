package app.haiyunshan.whatsnote.article.entry;

import com.google.gson.annotations.SerializedName;

public class FormulaEntry extends ParagraphEntry {

    public static final String TYPE = "formula";

    @SerializedName("formula")
    String formula;

    @SerializedName("latex")
    String latex;

    public FormulaEntry(String id, String formula, String latex) {
        super(id);
        this.type = TYPE;

        this.formula = formula;
        this.latex = latex;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getLatex() {
        return latex;
    }

    public void setLatex(String latex) {
        this.latex = latex;
    }
}
