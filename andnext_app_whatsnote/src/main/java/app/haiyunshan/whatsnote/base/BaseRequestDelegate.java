package app.haiyunshan.whatsnote.base;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import java.util.function.BiConsumer;

public abstract class BaseRequestDelegate implements BiConsumer<Integer, Intent> {

    private static int COUNTER = 1;

    protected int requestCode;

    protected Fragment parent;
    protected Activity context;

    public BaseRequestDelegate(Fragment f) {
        this.context = f.getActivity();
        this.parent = f;

        this.requestCode = COUNTER;

        {
            int counter = COUNTER;
            ++counter;
            counter %= 1000;
            COUNTER = counter;
        }
    }

    public int getRequestCode() {
        return requestCode;
    }

    public abstract boolean request();

    public abstract void accept(Integer resultCode, Intent data);
}
