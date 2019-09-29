package club.andnext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import java.util.Optional;

public class PlaceholderView extends View {

    int referId;

    public PlaceholderView(Context context) {
        this(context, null);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlaceholderView);
            this.referId = a.getResourceId(R.styleable.PlaceholderView_referTo, 0);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Optional<View> view = this.getReferView();
        if (view.isPresent()) {
            View v = view.get();
            this.setMeasuredDimension(v.getMeasuredWidth(), v.getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    Optional<View> getReferView() {
        View view = null;

        if (this.referId != 0) {

            View parent = this;
            while (parent.getParent() != null && parent.getParent() instanceof View) {
                parent = (View) parent.getParent();
                view = parent.findViewById(referId);
                if (view != null) {
                    break;
                }
            }
        }

        return Optional.ofNullable(view);
    }


}
