package club.andnext.snapshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class Snapshot {

    View view;

    float memoryFactor = 0.5f;

    public Snapshot(View view) {
        this(view, 0.5f);
    }

    public Snapshot(View view, float factor) {
        this.view = view;
        this.memoryFactor = (factor > 0.9f || factor < 0.1f)? 0.5f: factor;
    }

    public Bitmap apply() {
        Mode mode = chooseMode(view);
        if (mode == null) {
            return null;
        }

        Bitmap target = Bitmap.createBitmap(mode.mWidth, mode.mHeight, mode.mConfig);
        Canvas canvas = new Canvas(target);
        if (mode.mWidth != mode.mSourceWidth) {
            float scale = 1.f * mode.mWidth / mode.mSourceWidth;
            canvas.scale(scale, scale);
        }
        view.draw(canvas);

        return target;
    }

    Mode chooseMode(View view) {
        Mode mode = chooseMode(view.getWidth(), view.getHeight());
        return mode;
    }

    Mode chooseMode(int width, int height) {

        Mode mode;

        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long remain = max - total; // 剩余可用内存
        remain = (long)(memoryFactor * remain);

        int w = width;
        int h = height;
        while (true) {

            // 尝试4个字节
            long memory = 4 * w * h;
            if (memory <= remain) {
                if (memory <= remain / 3) { // 优先保证保存后的图片文件不会过大，有利于分享
                    mode = new Mode(Bitmap.Config.ARGB_8888, w, h, width, height);

                    break;
                }
            }

            // 尝试2个字节
            memory = 2 * w * h;
            if (memory <= remain) {
                mode = new Mode(Bitmap.Config.RGB_565, w, h, width, height);
                break;
            }

            // 判断是否可以继续
            if (w % 3 != 0) {
                h = (int)(remain / 2 / w); // 计算出最大高度
                h = h / 2 * 2; // 喜欢偶数

                mode = new Mode(Bitmap.Config.RGB_565, w, h, width, height);
                break;
            }

            // 缩减到原来的2/3
            w = w * 2 / 3;
            h = h * 2 / 3;
        }

        return mode;
    }

    /**
     *
     */
    public static final class Mode {

        Bitmap.Config mConfig;

        int mWidth;
        int mHeight;

        int mSourceWidth;
        int mSourceHeight;

        Mode(Bitmap.Config config, int width, int height, int srcWidth, int srcHeight) {
            this.mConfig = config;

            this.mWidth = width;
            this.mHeight = height;

            this.mSourceWidth = srcWidth;
            this.mSourceHeight = srcHeight;
        }
    }
}
