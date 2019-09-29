package app.haiyunshan.whatsnote.base;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class RequestResultDelegate extends BaseRequestDelegate {

    BiFunction<Fragment, Integer, Boolean> request;
    BiConsumer<Integer, Intent> result;

    public RequestResultDelegate(Fragment f,
                                 @Nullable BiFunction<Fragment, Integer, Boolean> request) {
        this(f, request, null);
    }

    public RequestResultDelegate(Fragment f,
                                 @Nullable BiFunction<Fragment, Integer, Boolean> request,
                                 @Nullable BiConsumer<Integer, Intent> result) {
        super(f);

        this.request = request;
        this.result = result;
    }

    @Override
    public boolean request() {
        if (request != null) {
            return request.apply(parent, this.getRequestCode());
        }

        return false;
    }

    @Override
    public void accept(Integer resultCode, Intent data) {
        if (result != null) {
            result.accept(resultCode, data);
        }
    }
}
