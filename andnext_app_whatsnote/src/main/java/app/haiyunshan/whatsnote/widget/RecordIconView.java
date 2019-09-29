package app.haiyunshan.whatsnote.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import club.andnext.glide.RoundedMask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

public class RecordIconView extends AppCompatImageView {

    int radius;
    Drawable mask;

    public RecordIconView(Context context) {
        this(context, null);
    }

    public RecordIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        {
            this.radius = 6;
        }


        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordIconView);

            this.radius = a.getDimensionPixelSize(R.styleable.RecordIconView_radius, radius);
            this.mask = a.getDrawable(R.styleable.RecordIconView_mask);

            a.recycle();
        }

        {
            if (mask == null) {
                mask = context.getDrawable(R.drawable.shape_icon_mask);
            }

            ((GradientDrawable)mask).setCornerRadius(radius);
        }

        {
            this.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorFolder)));
        }

    }

    public void setIcon(RecordEntity entity) {
        if (entity.isDirectory()) {
            this.setImageTintMode(PorterDuff.Mode.SRC_IN);

            this.setImageResource(R.drawable.ic_folder_white_24dp);
        } else {
            this.setImageTintMode(null);

            this.setSnapshot(entity);
        }
    }

    void setSnapshot(RecordEntity entity) {
        RequestOptions options =
                RequestOptions.signatureOf(new ObjectKey(entity.getSignature()))
                .transforms(new RoundedMask((GradientDrawable) mask))
                .error(R.drawable.shape_note_icon)
                .placeholder(R.drawable.shape_note_icon);

        Glide.with(this)
                .load(entity.getSnapshot())
                .apply(options)
                .into(this);
    }

}
