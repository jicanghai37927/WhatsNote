package app.haiyunshan.whatsandroid;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestTouchDialogActivity extends AppCompatActivity {

    View sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_touch_dialog);

        this.sendBtn = findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(this::onSendClick);
        sendBtn.setOnLongClickListener(this::onSendLongClick);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.w("AA", "activity touch event = " + ev.toString());

        return super.dispatchTouchEvent(ev);
    }

    void onSendClick(View view) {

    }

    boolean onSendLongClick(View view) {
        showDialog();

        return true;
    }

    void showDialog() {
        TapeDialog dialog = new TapeDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    /**
     *
     */
    private static class TapeDialog extends Dialog {

        public TapeDialog(@NonNull Context context) {
            this(context, 0);
        }

        public TapeDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        protected TapeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
            Log.w("AA", "dialog touch event = " + ev.toString());

            return super.dispatchTouchEvent(ev);
        }
    }
}
