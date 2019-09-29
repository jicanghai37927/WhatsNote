package club.andnext.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.utils.BlurUtils;
import com.google.android.material.appbar.AppBarLayout;

public class BlurDrawable extends Drawable {

    Destination dest;

    Bitmap bitmap;
    Canvas canvas;

    View target;
    View source;

    Context context;

    public BlurDrawable(View target, AppBarLayout appBarLayout, RecyclerView recyclerView) {
        this.context = target.getContext();

        this.target = target;
        this.source = (View)appBarLayout.getParent();

        appBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener());
        recyclerView.addOnScrollListener(new OnScrollListener());
    }

    void update() {
        int width = target.getWidth();
        int height = target.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        if (dest == null) {
            this.dest = new Destination(context.getResources(), width, height);

            this.bitmap = Bitmap.createBitmap(dest.bitmap);
            this.canvas = new Canvas(bitmap);
        }

        {
            float scale = 1.f * bitmap.getWidth() / target.getWidth();

            bitmap.eraseColor(Color.WHITE);

            canvas.save();

            canvas.scale(scale, scale);
            source.draw(canvas);

            canvas.restore();
        }

        {
            Bitmap blur = BlurUtils.blur(context, bitmap, 12, false);
            dest.draw(blur);
            blur.recycle();
        }

        {
            target.invalidate();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (dest == null) {
            return;
        }

        dest.drawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    /**
     *
     */
    private class OnOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            update();
        }
    }

    /**
     *
     */
    private class OnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            update();
        }
    }

    /**
     *
     */
    private static class Destination {

        Bitmap bitmap;
        Canvas canvas;

        BitmapDrawable drawable;

        Destination(Resources resources, int width, int height) {
            this.bitmap = Bitmap.createBitmap(width / 12, height / 12, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);

            this.drawable = new BitmapDrawable(resources, bitmap);
            drawable.setBounds(0, 0, width, height);
        }

        void draw(Bitmap bitmap) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

    }

}
