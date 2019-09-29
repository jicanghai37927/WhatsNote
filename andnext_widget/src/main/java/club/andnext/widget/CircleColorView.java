package club.andnext.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class CircleColorView extends AppCompatImageView {

    int replaceColor;   // color for transparent
    int color;

    public CircleColorView(Context context) {
        this(context, null);
    }

    public CircleColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        {
            this.replaceColor = getContext().getColor(R.color.anc_replace_circle_color);
        }

        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleColorView);
            this.replaceColor = a.getColor(R.styleable.CircleColorView_replaceColor, replaceColor);
            this.color = a.getColor(R.styleable.CircleColorView_android_color, Color.TRANSPARENT);
            a.recycle();
        }

        {
            this.setColor(color);
            this.setReplaceColor(replaceColor);
        }

    }

    public void setReplaceColor(int color) {
        this.replaceColor = color;
    }

    public int getReplaceColor() {
        return this.replaceColor;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;

        if (color == Color.TRANSPARENT) {

            int c = this.getReplaceColor();

            int resId = R.drawable.anc_ic_circle_color_stroke;
            this.setImageResource(resId);
            this.setImageTintList(ColorStateList.valueOf(c));

        } else {

            int c = color;

            int resId = R.drawable.anc_ic_circle_color_solid;
            this.setImageResource(resId);
            this.setImageTintList(ColorStateList.valueOf(c));

        }

    }

}
