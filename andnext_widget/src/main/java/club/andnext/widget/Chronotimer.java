package club.andnext.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;

public class Chronotimer extends androidx.appcompat.widget.AppCompatTextView {

    boolean isRunning = false;

    long base = 0; // base time in milliseconds

    long start; // start time in milliseconds
    long end; // end time in milliseconds

    long max; // max time

    PreDrawListener preDrawListener;
    private boolean mPreDrawRegistered;

    public Chronotimer(Context context) {
        this(context, null);
    }

    public Chronotimer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public Chronotimer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.preDrawListener = new PreDrawListener();
    }

    public void start() {
        this.isRunning = true;

        this.start = System.currentTimeMillis();
        this.end = start;

        this.registerForPreDraw();
        this.invalidate();
    }

    public void stop() {
        this.isRunning = false;

        this.end = System.currentTimeMillis();

        this.unregisterForPreDraw(); // must unregister OnPreDrawListener, or else draw all the time.
        this.invalidate();
    }

    public void setBase(long base) {
        this.base = base;

        CharSequence text = format(base);
        setText(text);
    }

    public void setMax(long max) {
        this.max = max;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isRunning) {
            registerForPreDraw();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        this.unregisterForPreDraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isRunning) {
            this.invalidate();
        }

//        Log.w("AA", "Chronotimer onDraw = " + isRunning);
    }

    public CharSequence format(long milliseconds) {
        long elapse = milliseconds;
        elapse /= 1000; // seconds

        int hours = (int)(elapse / 3600);
        int seconds = (int)(elapse % 3600);
        int minute = (seconds / 60);
        seconds = seconds % 60;

        String text;
        if (hours > 0) {
            text = String.format("%d:%02d:%02d", hours, minute, seconds);
        } else {
            text = String.format("%d:%02d", minute, seconds);
        }

        return text;
    }

    private void registerForPreDraw() {
        if (!mPreDrawRegistered) {

            getViewTreeObserver().addOnPreDrawListener(preDrawListener);

            mPreDrawRegistered = true;
        }
    }

    private void unregisterForPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);

        mPreDrawRegistered = false;
    }

    /**
     *
     */
    private class PreDrawListener implements ViewTreeObserver.OnPreDrawListener {

        @Override
        public boolean onPreDraw() {
            long duration = base;

            if (isRunning) {
                end = System.currentTimeMillis();
                duration += (end - start);
            }

            if (max > 0) {
                duration = (duration > max)? max: duration;
            }

            CharSequence text = format(duration);
            setText(text);

            return true;
        }
    }
}
