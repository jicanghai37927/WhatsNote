package app.haiyunshan.widget;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsandroid.R;

public class WaveView extends FrameLayout {

    int waveWidth = 4; // px
    int waveDash = 2; // px
    int speed = 10; // 20 wave per second，must be [1, 2, 5, 10, 20, 25, 50, 100]
    float duration = 8; // seconds

    float time = 0.f; // current time in seconds
    IntArray decibel;
    int maxDecibel;

    Drawable drawable;
    GradientDrawable clipDrawable;

    OffScreen offScreen;

    public WaveView(@NonNull Context context) {
        this(context, null);
    }

    public WaveView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WaveView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {
            this.decibel = new IntArray();
            this.drawable = context.getResources().getDrawable(R.drawable.ic_drag_handle_white_24dp, null);
            this.clipDrawable = (GradientDrawable)(context.getResources().getDrawable(R.drawable.anc_shape_clip_tape, null));
        }

        {
            this.maxDecibel = (int)(20 * Math.log10(Math.abs(Short.MIN_VALUE)));
        }
    }

    public void addDecibel(int value) {
        this.decibel.add(value);
        this.time += (1.f / 10);

        this.postInvalidate();
    }

    public void clear() {
        this.decibel.clear();
        this.time = 0;

        this.postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (offScreen != null) {
            if (offScreen.bitmap.getWidth() != w || offScreen.bitmap.getHeight() != h) {
                offScreen.recycle();
                offScreen = null;
            }
        }

        if (offScreen == null) {
            offScreen = new OffScreen(w, h);
        }

        if (clipDrawable != null) {
            float y = w / 2.f;
            float x = y / 0.618f;
            float[] radii = new float[] {
                    x, y,
                    0, 0,
                    0, 0,
                    x, y
            };

            clipDrawable.setCornerRadii(radii);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = waveWidth + waveDash; // wave
        width *= speed; // wave in a second
        width *= duration; // duration

        int height = getMeasuredHeight();
        height = height / 2 * 2;
        height = (Math.max(height, 2));

        this.setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (offScreen != null) {
            offScreen.draw();

            canvas.drawBitmap(offScreen.screen, 0, 0, null);
        }

    }

    /**
     *
     */
    private class OffScreen {

        Bitmap bitmap;
        Canvas canvas;

        Bitmap screen;
        Canvas screenCanvas;

        private Paint paint = new Paint();

        public OffScreen(int width, int height) {

            {
                this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setHasAlpha(true);

                this.canvas = new Canvas(bitmap);

            }

            {
                this.screen = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                screen.setHasAlpha(true);

                this.screenCanvas = new Canvas(screen);
            }

            {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }
        }

        void recycle() {
            if (bitmap != null) {
                bitmap.recycle();

                bitmap = null;
                canvas = null;
            }

            if (screen != null) {
                screen.recycle();

                screen = null;
                screenCanvas = null;
            }
        }

        void draw() {
            {
                bitmap.eraseColor(Color.TRANSPARENT);
                this.draw(canvas);
            }

            {
                clipDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                clipDrawable.draw(screenCanvas);

                screenCanvas.drawBitmap(bitmap, 0, 0, paint);
            }
        }

        void draw(Canvas canvas) {
            int max = maxDecibel;
            int prefer = 42;

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int waveSize = waveWidth + waveDash;
            int waveHeight;

            int x = waveSize * speed;
            x *= time;
            x *= -1;
            x += width;

            int y;

            for (int i = 0, size = decibel.size; i < size; i++) {
                if ((x + waveWidth) < 0) {
                    x += waveSize;

                    continue;
                }

                int value = decibel.get(i);

                if (value <= prefer) {
                    waveHeight = 2;
                } else {
                    waveHeight = height * (value - prefer) / (max - prefer);
                    waveHeight *= 1.5f;
                    waveHeight = waveHeight / 2 * 2;
                    waveHeight = (waveHeight < 2) ? 2 : waveHeight;
                    waveHeight = (waveHeight > height) ? height : waveHeight;
                }

                y = (height - waveHeight) / 2;
                drawable.setBounds(x, y, x + waveWidth, y + waveHeight);
                drawable.draw(canvas);

                {
                    x += waveSize;
                }
            }
        }

    }

    /**
     *
     */
    private static class IntArray {

        private int[] array;
        private int size;

        public IntArray() {
            this.array = new int[4 * 1024];
            this.size = 0;
        }

        public int get(int index) {
            return array[index];
        }

        public int size() {
            return this.size;
        }

        public void clear() {
            this.size = 0;
        }

        public void add(int value) {
            if (size == array.length) {
                expend();
            }

            array[size] = value;
            ++size;
        }

        public void set(int value, int index) {
            array[index] = value;
        }

        void expend() {
            int[] tmp = new int[array.length + 4 * 1024];
            System.arraycopy(array, 0, tmp, 0, array.length);
            this.array = tmp;
        }
    }

}
