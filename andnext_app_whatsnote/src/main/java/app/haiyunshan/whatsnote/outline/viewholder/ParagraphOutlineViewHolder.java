package app.haiyunshan.whatsnote.outline.viewholder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.outline.entity.ParagraphOutlineEntity;
import app.haiyunshan.whatsnote.outline.helper.OutlineHelper;

public class ParagraphOutlineViewHolder extends BaseOutlineViewHolder<ParagraphOutlineEntity> {

    TextView textView;

    @Keep
    public ParagraphOutlineViewHolder(OutlineHelper helper, View itemView) {
        super(helper, itemView);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        viewStub.setLayoutResource(R.layout.layout_paragraph_outline_item);
        this.textView = (viewStub.inflate().findViewById(R.id.tv_name));
    }

    @Override
    public void onBind(ParagraphOutlineEntity item, int position) {
        super.onBind(item, position);

        iconView.setImageResource(R.drawable.ic_paragraph_outline);
        textView.setText(item.getText());
    }
}
