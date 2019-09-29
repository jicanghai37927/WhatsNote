package app.haiyunshan.whatsnote.article.delegate;

import android.content.Intent;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.ComposeFormulaActivity;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.FormulaEntity;
import app.haiyunshan.whatsnote.article.insertion.BaseInsertion;
import app.haiyunshan.whatsnote.article.insertion.FormulaInsertion;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import club.andnext.utils.SoftInputUtils;

import static android.app.Activity.RESULT_OK;

public class FormulaDelegate extends BaseRequestDelegate {

    FormulaEntity entity;

    BaseInsertion.Callback callback;

    public FormulaDelegate(Fragment f, FormulaEntity entity, BaseInsertion.Callback callback) {
        super(f);

        this.entity = entity;
        this.callback = callback;
    }

    @Override
    public boolean request() {
        SoftInputUtils.hide(context);

        try {
            String formula = entity == null? "": entity.getFormula();
            ComposeFormulaActivity.startForResult(parent, formula, getRequestCode());

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    @Override
    public void accept(Integer resultCode, Intent data) {

        if ((resultCode != RESULT_OK) || (data == null)) {
            return;
        }

        String formula = data.getStringExtra("formula");
        if (TextUtils.isEmpty(formula)) {
            return;
        }

        String latex = data.getStringExtra("latex");
        if (TextUtils.isEmpty(latex)) {
            return;
        }

        if (entity == null) {
            new FormulaInsertion(callback, formula, latex).execute();
        } else {
            entity.setFormula(formula);
            entity.setLatex(latex);

            Document document = callback.getDocument();
            int index = document.indexOf(entity);
            if (index >= 0) {
                callback.getRecyclerView().getAdapter().notifyItemChanged(index);
            }

        }
    }

}
