package app.haiyunshan.whatsnote.rxjava2;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import io.reactivex.disposables.Disposable;
import retrofit2.http.PUT;

import java.util.ArrayList;
import java.util.List;

public class RxDisposer implements LifecycleObserver {

    public static final int EVENT_PAUSE     = Lifecycle.Event.ON_PAUSE.ordinal();
    public static final int EVENT_DESTROY   = Lifecycle.Event.ON_DESTROY.ordinal();

    ArrayList<Disposable> destroy;
    ArrayList<Disposable> pause;

    public RxDisposer(FragmentActivity context) {
        this.destroy = new ArrayList<>();
        this.pause = new ArrayList<>();

        context.getLifecycle().addObserver(this);
    }

    public void add(Disposable d) {
        this.add(d, EVENT_DESTROY);
    }

    public void add(Disposable d, int event) {
        List<Disposable> list = destroy;
        if (event == EVENT_PAUSE) {
            list = pause;
        }

        list.add(d);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        dispose(pause);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        dispose(destroy);
    }

    static void dispose(List<Disposable> list) {
        list.stream().forEach(d -> d.dispose());
        list.clear();
    }
}
