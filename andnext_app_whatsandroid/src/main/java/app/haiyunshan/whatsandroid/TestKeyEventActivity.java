package app.haiyunshan.whatsandroid;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestKeyEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_key_event);

        EditText editText = findViewById(R.id.edit_text);

        Log.w("AA", "TestKeyEventActivity");

        editText.setOnKeyListener((v, keyCode, event) -> {

            Log.w("AA", "onKey");

            int action = event.getAction();
            switch (action) {
                case KeyEvent.ACTION_DOWN: {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        Log.w("AA", "setOnKeyListener KEYCODE_DEL");
                    }

                    break;
                }
            }

            return false;
        });

        editText.setOnEditorActionListener((v, actionId, event) -> {

            Log.w("AA", "setOnEditorActionListener");


            int action = event.getAction();
            switch (action) {
                case KeyEvent.ACTION_DOWN: {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                        Log.w("AA", "setOnEditorActionListener KEYCODE_DEL");
                    }

                    break;
                }
            }

            return false;
        });
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return super.onKeyShortcut(keyCode, event);
    }
}
