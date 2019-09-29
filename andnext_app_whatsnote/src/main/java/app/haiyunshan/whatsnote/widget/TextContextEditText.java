package app.haiyunshan.whatsnote.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.function.Consumer;

public class TextContextEditText extends AppCompatEditText {

    Consumer<Integer> onTextContextListener;

    public TextContextEditText(Context context) {
        this(context, null);
    }

    public TextContextEditText(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public TextContextEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean result = super.onTextContextMenuItem(id);

        if (onTextContextListener != null) {
            onTextContextListener.accept(id);
        }

        return result;
    }

    public void setOnTextContextListener(Consumer<Integer> listener) {
        this.onTextContextListener = listener;
    }
}
