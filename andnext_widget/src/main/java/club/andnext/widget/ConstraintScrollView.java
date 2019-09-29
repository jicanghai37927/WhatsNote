package club.andnext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class ConstraintScrollView extends NestedScrollView {

    int maxHeight;

    public ConstraintScrollView(@NonNull Context context) {
        this(context, null);
    }

    public ConstraintScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConstraintScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConstraintScrollView);
            this.maxHeight = a.getDimensionPixelSize(R.styleable.ConstraintScrollView_maxHeight, -1);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (maxHeight > 0) {
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();
            height = (height > maxHeight)? maxHeight: height;

            this.setMeasuredDimension(width, height);
        }


    }
}
