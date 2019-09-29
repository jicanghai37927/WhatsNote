package app.haiyunshan.whatsnote.outline.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.outline.entity.FormulaOutlineEntity;
import app.haiyunshan.whatsnote.outline.helper.OutlineHelper;
import ru.noties.jlatexmath.JLatexMathView;

public class FormulaOutlineViewHolder extends BaseOutlineViewHolder<FormulaOutlineEntity> {

    JLatexMathView pictureView;

    @Keep
    public FormulaOutlineViewHolder(OutlineHelper helper, View itemView) {
        super(helper, itemView);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        viewStub.setLayoutResource(R.layout.layout_formula_outline_item);
        this.pictureView = (viewStub.inflate().findViewById(R.id.iv_picture));
    }

    @Override
    public void onBind(FormulaOutlineEntity item, int position) {
        super.onBind(item, position);

        iconView.setImageResource(R.drawable.ic_insert_formula_white_24dp);

        try {
            pictureView.setLatex(item.getLatex(), true);
        } catch (Exception e) {

        }

    }

}
