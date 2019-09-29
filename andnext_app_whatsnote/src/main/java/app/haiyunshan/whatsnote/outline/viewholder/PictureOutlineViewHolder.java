package app.haiyunshan.whatsnote.outline.viewholder;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.outline.entity.PictureOutlineEntity;
import app.haiyunshan.whatsnote.outline.helper.OutlineHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PictureOutlineViewHolder extends BaseOutlineViewHolder<PictureOutlineEntity> {

    ImageView pictureView;

    @Keep
    public PictureOutlineViewHolder(OutlineHelper helper, View itemView) {
        super(helper, itemView);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        viewStub.setLayoutResource(R.layout.layout_picture_outline_item);
        this.pictureView = (viewStub.inflate().findViewById(R.id.iv_picture));
        pictureView.setClipToOutline(true);
    }

    @Override
    public void onBind(PictureOutlineEntity item, int position) {
        super.onBind(item, position);

        iconView.setImageResource(R.drawable.ic_picture_outline);

        RequestOptions options = createRequestOptions(item.getParent());

        Glide.with(pictureView)
                .load(item.getUri())
                .apply(options)
//                .apply(RequestOptions.bitmapTransform(new SupportRSBlurTransformation(12, 1)))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(12, 1)))
                .into(pictureView);
    }

    RequestOptions createRequestOptions(PictureEntity entity) {
        RequestOptions options = new RequestOptions();

        // picture size
        int width = entity.getWidth();
        int height = entity.getHeight();

        // sample it for 1, 2, 4, 8, ...
        {
            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int maxWidth = metrics.widthPixels / 2;
            int maxHeight = metrics.heightPixels / 2;

            int widthSampleFactor = getSampleFactor(width, maxWidth);
            int heightSampleFactor = getSampleFactor(height, maxHeight);

            int sampleFactor = Math.max(widthSampleFactor, heightSampleFactor);
            sampleFactor = Math.max(1, Integer.highestOneBit(sampleFactor));

            if (sampleFactor > 1) {
                width /= sampleFactor;
                height /= sampleFactor;
            }
        }

        {
            options.dontTransform();
            options.override(width, height);
            options.downsample(DownsampleStrategy.FIT_CENTER);
        }

        return options;
    }

    static int getSampleFactor(int source, int target) {
        int scale = 1;

        while ((source / scale) > target) {
            scale *= 2;
        }

        return scale;
    }

}
