package club.andnext.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import androidx.appcompat.widget.AppCompatEditText;

public class KeyShortcutEdit extends AppCompatEditText {

    OnKeyShortcutListener keyShortcutListener;

    public KeyShortcutEdit(Context context) {
        this(context, null);
    }

    public KeyShortcutEdit(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public KeyShortcutEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnKeyShortcutListener(OnKeyShortcutListener listener) {
        this.keyShortcutListener = listener;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            id = android.R.id.pasteAsPlainText;
        }

        return super.onTextContextMenuItem(id);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if (keyShortcutListener != null) {
            if (keyShortcutListener.onKeyShortcut(keyCode, event)) {
                return true;
            }
        }

        return super.onKeyShortcut(keyCode, event);
    }

    /**
     *
     */
    public interface OnKeyShortcutListener {

        boolean onKeyShortcut(int keyCode, KeyEvent event);
    }
}
