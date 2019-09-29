package app.haiyunshan.whatsnote.base;

import android.content.Intent;

public class RequestResultManager {

    BaseRequestDelegate delegate;

    public RequestResultManager() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (delegate == null) {
            return;
        }

        if (delegate.getRequestCode() == requestCode) {
            delegate.accept(resultCode, data);
        }
    }

    public void request(BaseRequestDelegate delegate) {
        boolean result = delegate.request();
        this.delegate = result? delegate: null;

    }

}
