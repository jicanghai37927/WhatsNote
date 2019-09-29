package club.andnext.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 *
 */
public class SoftInputHelper implements LifecycleObserver,
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = SoftInputHelper.class.getSimpleName();

    int bottom;

    boolean visible;

    Rect rect;

    SoftInputObservable softInputObservable;

    FragmentActivity context;

    public SoftInputHelper(@NonNull FragmentActivity context) {
        this.context = context;

        this.bottom = 0;
        this.visible = false;

        this.rect = new Rect();

        context.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        ViewTreeObserver observer = getViewTreeObserver(context);
        if (observer != null && observer.isAlive()) {
            observer.addOnGlobalLayoutListener(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        ViewTreeObserver observer = getViewTreeObserver(context);
        if (observer != null && observer.isAlive()) {
            observer.removeOnGlobalLayoutListener(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        context.getLifecycle().removeObserver(this);
    }

    @Override
    public void onGlobalLayout() {

        {
            this.updateBottom(context);
        }

        View target = getView(context);
        if (target != null) {

            target.getGlobalVisibleRect(rect);
            Log.v(TAG, "target bottom = " + rect.bottom);

            boolean result = rect.bottom < bottom;
            if (this.visible ^ result) {
                this.visible = result;

                if (softInputObservable != null) {
                    softInputObservable.notifySoftInputChanged(this, visible);
                }
            }

        }
    }

    public boolean isVisible() {
        return visible;
    }

    public int getBottom() {
        return bottom;
    }

    public void addOnSoftInputListener(OnSoftInputListener listener) {
        if (softInputObservable == null) {
            softInputObservable = new SoftInputObservable();
        }

        softInputObservable.registerObserver(listener);
    }

    public void removeOnSoftInputListener(OnSoftInputListener listener) {
        if (softInputObservable != null) {
            softInputObservable.unregisterObserver(listener);
        }
    }

    void updateBottom(Activity activity) {
        View view = getView(activity);
        if (view != null) {
            view.getGlobalVisibleRect(rect);

            bottom = (bottom < view.getBottom())? view.getBottom(): bottom;
            bottom = (bottom < rect.bottom)? rect.bottom: bottom;
        }

        Log.v(TAG, "bottom = " + bottom);
    }

    View getView(Activity activity) {
        View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        return view;
    }

    ViewTreeObserver getViewTreeObserver(Activity activity) {
        View view = getView(activity);
        if (view == null) {
            return null;
        }

        return view.getViewTreeObserver();
    }

    /**
     *
     */
    static class SoftInputObservable extends ObservableHelper<OnSoftInputListener> {

        void notifySoftInputChanged(final SoftInputHelper helper, final boolean visible) {
            mObservers.stream().forEach((action) -> action.onSoftInputChanged(helper, visible));
        }

    }

    /**
     *
     */
    public interface OnSoftInputListener {

        void onSoftInputChanged(SoftInputHelper helper, boolean visible);

    }
}
