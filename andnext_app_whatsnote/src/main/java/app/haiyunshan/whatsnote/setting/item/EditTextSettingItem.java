package app.haiyunshan.whatsnote.setting.item;

import android.text.Editable;
import android.text.TextWatcher;

public class EditTextSettingItem extends BaseSettingItem {

    CharSequence text;

    int minLines;
    int maxLines;

    boolean requestFocus;

    Callback callback;

    public EditTextSettingItem(CharSequence text) {
        super("");

        this.text = text;
        this.minLines = 3;
        this.maxLines = 14;

        this.requestFocus = true;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public int getMinLines() {
        return minLines;
    }

    public void setMinLines(int minLines) {
        this.minLines = minLines;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public boolean isRequestFocus() {
        return requestFocus;
    }

    public void setRequestFocus(boolean requestFocus) {
        this.requestFocus = requestFocus;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     *
     */
    public abstract static class Callback implements TextWatcher {

        public void onTextContextMenuItem(int id) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
