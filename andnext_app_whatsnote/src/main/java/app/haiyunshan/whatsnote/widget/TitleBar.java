package app.haiyunshan.whatsnote.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.haiyunshan.whatsnote.R;

public class TitleBar extends RelativeLayout {

    TextView negativeBtn;
    TextView titleView;
    TextView positiveBtn;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {
            int resource = R.layout.merge_title_bar;
            LayoutInflater.from(context).inflate(resource, this, true);
        }

        {
            this.negativeBtn = findViewById(R.id.btn_negative);
            this.titleView = findViewById(R.id.tv_title);
            this.positiveBtn = findViewById(R.id.btn_positive);
        }

        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);


            {
                String title = a.getString(R.styleable.TitleBar_title);
                titleView.setText(title);
            }


            a.recycle();
        }
    }

    public void setTitle(CharSequence text) {
        titleView.setText(text);
    }

    public void setPositiveButton(CharSequence text, final OnClickListener listener) {
        positiveBtn.setText(text);
        positiveBtn.setOnClickListener(listener);
    }

    public void setPositiveText(CharSequence text) {
        positiveBtn.setText(text);
    }

    public void setPositiveListener(final OnClickListener listener) {
        positiveBtn.setOnClickListener(listener);
    }

    public void setPositiveEnable(boolean value) {
        positiveBtn.setEnabled(value);
    }

    public void setNegativeButton(CharSequence text, final OnClickListener listener) {
        negativeBtn.setText(text);
        negativeBtn.setOnClickListener(listener);
    }

    public void setNegativeText(CharSequence text) {
        negativeBtn.setText(text);
    }

    public void setNegativeListener(final OnClickListener listener) {
        negativeBtn.setOnClickListener(listener);
    }

    public void setNegativeVisible(boolean visible) {
        negativeBtn.setVisibility(visible? View.VISIBLE: View.GONE);
    }

    public void setNegativeEnable(boolean value) {
        negativeBtn.setEnabled(value);
    }
}
