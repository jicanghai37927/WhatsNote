package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.NumericalSettingItem;

import java.util.Optional;

public class NumericalSettingViewHolder extends BaseSettingViewHolder<NumericalSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_numerical_list_item;

    View minusBtn;
    View plusBtn;

    @Keep
    public NumericalSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        {
            view.setOnClickListener(null);
            view.setClickable(false);
        }

        {
            this.minusBtn = view.findViewById(R.id.iv_minus);
            minusBtn.setOnClickListener(this::onMinusClick);

            this.plusBtn = view.findViewById(R.id.iv_plus);
            plusBtn.setOnClickListener(this::onPlusClick);
        }
    }

    @Override
    public void onBind(NumericalSettingItem item, int position) {
        super.onBind(item, position);

        updateText(item);
        updateButtons(item);


    }

    void onMinusClick(View view) {
        NumericalSettingItem item = this.getItem();
        if (item == null) {
            return;
        }

        item.decrease();

        updateText(item);
        updateButtons(item);

        if (item.getConsumer() != null) {
            item.getConsumer().accept(item.getValue());
        }
    }

    void onPlusClick(View view) {
        NumericalSettingItem item = this.getItem();
        if (item == null) {
            return;
        }

        item.increase();

        updateText(item);
        updateButtons(item);

        if (item.getConsumer() != null) {
            item.getConsumer().accept(item.getValue());
        }
    }

    void updateText(NumericalSettingItem item) {

        CharSequence text = Optional.ofNullable(item.getTransform())
                .orElse((value -> String.valueOf(value)))
                .apply(item.getValue());
        textView.setText(text);
    }

    void updateButtons(NumericalSettingItem item) {
        minusBtn.setEnabled(!item.isMin());
        plusBtn.setEnabled(!item.isMax());
    }
}
