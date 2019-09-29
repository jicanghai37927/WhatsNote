package club.andnext.snapshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

public class InstantShot {

    Bitmap bitmap;
    Canvas canvas;

    View view;

    public InstantShot(View view, int width, int height) {
        this.view = view;

        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        this.canvas = new Canvas(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void update() {
        bitmap.eraseColor(Color.WHITE);

        float scale = 1.f * bitmap.getWidth() / view.getWidth();

        canvas.save();
        canvas.scale(scale, scale);

        view.draw(canvas);

        canvas.restore();
    }

    public void recycle() {

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;

            canvas = null;
        }
    }
}
