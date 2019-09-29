package app.haiyunshan.whatsnote.article.viewholder;

import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import club.andnext.text.IndentTextWatcher;
import club.andnext.widget.ParagraphEdit;

/**
 *
 */
public class ParagraphViewHolder extends ComposeViewHolder<ParagraphEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_paragraph_list_item;

    ParagraphEdit editText;

    ParagraphKeyListener keyListener;

    @Keep
    public ParagraphViewHolder(Callback callback, View itemView) {
        super(callback, itemView);

        this.keyListener = new ParagraphKeyListener(this);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        {
            this.editText = view.findViewById(R.id.edit_paragraph);
            editText.addTextChangedListener(new IndentTextWatcher());
        }

        {
            this.setTextChangeListener(new TextChangeListener(this, editText));
        }
    }

    @Override
    public void onBind(ParagraphEntity item, int position) {
        super.onBind(item, position);

        {
            if (!callback.isEnable()) {
                editText.setTextIsSelectable(callback.isEnable());

                editText.setFocusable(callback.isEnable());
                editText.setFocusableInTouchMode(callback.isEnable());
            }

            if (editText.isEnabled() ^ callback.isEnable()) {
                editText.setEnabled(callback.isEnable());
            }

            if (editText.getShowSoftInputOnFocus() ^ callback.isEnable()) {
                editText.setShowSoftInputOnFocus(callback.isEnable());
            }
        }

        {
            // set break strategy to request layout
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editText.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
            }

            editText.setOnKeyListener(keyListener);
        }

        {
            editText.setText(item.getText());
        }

    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
    }

    @Override
    public void save() {
        entity.setText(editText.getText());
    }

    public CharSequence[] split() {
        if (!editText.isFocused()) {
            return null;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start != end) {
            return null;
        }

        int position = start;
        CharSequence text = editText.getText();

        CharSequence first;
        CharSequence second;
        if ((position > 0) && (text.charAt(position - 1) == '\n')) {

            first = text.subSequence(0, position - 1);
            second = text.subSequence(position, text.length());

        } else if ((position < text.length()) && (text.charAt(position) == '\n')) {

            first = text.subSequence(0, position);
            second = text.subSequence(position + 1, text.length());

        } else {

            first = text.subSequence(0, position);
            second = text.subSequence(position, text.length());

        }

        return new CharSequence[] { first, second };
    }

    public void setText(CharSequence text) {
        entity.setText(text);

        editText.setText(text);

        callback.afterTextChanged(this, editText);
    }

    public Editable getText() {
        return editText.getText();
    }

    public int length() {
        return editText.length();
    }

    public void setSelection(int index) {
        editText.setSelection(index);
    }

    public void setSelection(int start, int end) {
        editText.setSelection(start, end);
    }

    public int getSelectionStart() {
        return editText.getSelectionStart();
    }

    public int getSelectionEnd() {
        return editText.getSelectionEnd();
    }

    public void requestFocus() {
        editText.requestFocus();
    }

    int getLine() {
        int top = itemView.getTop();
        if (top >= 0) {
            return 0;
        }

        int vertical = Math.abs(top);

        Rect rect = new Rect();
        ((ViewGroup)(itemView)).offsetDescendantRectToMyCoords(editText, rect);
        vertical -= rect.top;
        if (vertical <= 0) {
            return 0;
        }

        Layout layout = editText.getLayout();
        int line = layout.getLineForVertical(vertical);
        return line;
    }

    public int getOffset() {

        int line = getLine();
        if (line == 0) {
            return 0;
        }

        int offset = editText.getLayout().getOffsetForHorizontal(line, 0);
        return offset;
    }

    public int getVertical(int offset) {
        Layout layout = editText.getLayout();

        int line = layout.getLineForOffset(offset);
        int top = layout.getLineTop(line);

        Rect rect = new Rect();
        ((ViewGroup)(itemView)).offsetDescendantRectToMyCoords(editText, rect);

        top += rect.top;
        return top;
    }

    /**
     *
     */
    private static class ParagraphKeyListener implements View.OnKeyListener {

        ParagraphViewHolder parent;

        ParagraphKeyListener(ParagraphViewHolder holder) {
            parent = holder;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            switch (action) {
                case KeyEvent.ACTION_DOWN: {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        int start = parent.getSelectionStart();
                        int end = parent.getSelectionEnd();
                        if ((start == end) && (start == 0)) {
                            parent.requestRemove();
                        }
                    }

                    break;
                }
            }

            return false;
        }

    }

}
