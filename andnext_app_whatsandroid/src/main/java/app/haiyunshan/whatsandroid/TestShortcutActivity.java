package app.haiyunshan.whatsandroid;

import android.view.KeyEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.widget.KeyShortcutEdit;

public class TestShortcutActivity extends AppCompatActivity {

    TextView resultView;
    KeyShortcutEdit editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_shortcut);

        this.resultView = findViewById(R.id.tv_result);

        this.editText = findViewById(R.id.edit_text);
        editText.setOnKeyShortcutListener(new KeyShortcutEdit.OnKeyShortcutListener() {
            @Override
            public boolean onKeyShortcut(int keyCode, KeyEvent event) {

//                handleKeyEvent(keyCode, event);

                return false;
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //这里注意要作判断处理，ActionDown、ActionUp都会回调到这里，不作处理的话就会调用两次
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            handleKeyEvent(event.getKeyCode(), event);
        }

        return super.dispatchKeyEvent(event);
    }

    void handleKeyEvent(int keyCode, KeyEvent event) {
        StringBuilder sb = new StringBuilder(100);

        if (event.hasModifiers(KeyEvent.META_CTRL_ON)) {
            sb.append("Ctrl ");
        }

        if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
            sb.append("Shift ");
        }

        if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
            sb.append("Alt ");
        }

        sb.append(event.getKeyCode());
        sb.append("/");
        sb.append(keyCode);

        resultView.setText(sb);
    }
}
