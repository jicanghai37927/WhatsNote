package app.haiyunshan.whatsnote.setting.viewholder;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.EditTextSettingItem;
import app.haiyunshan.whatsnote.widget.TextContextEditText;
import club.andnext.helper.ClearAssistMenuHelper;

import java.util.function.Supplier;

public class EditTextSettingViewHolder extends BaseSettingViewHolder<EditTextSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_edit_text_list_item;

    TextContextEditText editText;

    @Keep
    public EditTextSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        this.editText = view.findViewById(R.id.edit_text);

        ClearAssistMenuHelper.attach(editText);

        editText.setOnTextContextListener(this::onTextContextMenuItem);
        editText.addTextChangedListener(new EditTextListener());
    }

    @Override
    public void onBind(EditTextSettingItem item, int position) {
        super.onBind(item, position);

        {
            Supplier<CharSequence> s = item.getHintSupplier();
            CharSequence hint = (s == null)? item.getHint(): s.get();
            editText.setHint(hint);
        }

        {
            editText.setMinLines(item.getMinLines());
            editText.setMaxLines(item.getMaxLines());
        }

        {
            editText.setText(item.getText());
        }

        if (item.isRequestFocus()) {
            editText.requestFocus();
        }
    }

    void onTextContextMenuItem(int itemId) {
        EditTextSettingItem item = getItem();
        if (item != null) {
            item.setText(editText.getText());
        }

        EditTextSettingItem.Callback callback = getCallback();
        if (callback != null) {
            callback.onTextContextMenuItem(itemId);
        }
    }

    EditTextSettingItem.Callback getCallback() {
        EditTextSettingItem item = getItem();
        if (item != null) {
            return item.getCallback();
        }

        return null;
    }

    /**
     *
     */
    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            EditTextSettingItem.Callback callback = getCallback();
            if (callback != null) {
                callback.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditTextSettingItem.Callback callback = getCallback();
            if (callback != null) {
                callback.onTextChanged(s, start, before, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            EditTextSettingItem item = getItem();
            if (item != null) {
                item.setText(s);
            }

            EditTextSettingItem.Callback callback = getCallback();
            if (callback != null) {
                callback.afterTextChanged(s);
            }
        }
    }

}
