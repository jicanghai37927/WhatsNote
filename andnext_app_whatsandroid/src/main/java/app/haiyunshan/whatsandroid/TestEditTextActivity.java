package app.haiyunshan.whatsandroid;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import club.andnext.text.IndentTextWatcher;


public class TestEditTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_edit_text);

        EditText editText = findViewById(R.id.edit_text);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            Log.w("AA", "setOnEditorActionListener");

            return false;
        });

        editText.setOnKeyListener((v, keyCode, event) -> {
            Log.w("AA", "setOnKeyListener");

            return false;
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String str = String.format(", start = %1$d, count = %2$d, after = %3$d", start, count, after);

                Log.w("AA", "beforeTextChanged = " + s + str);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = String.format(", start = %1$d, before = %2$d, count = %3$d", start, before, count);

                Log.w("AA", "onTextChanged = " + s + str);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.addTextChangedListener(new IndentTextWatcher());
    }

    void onUndoClick(View view) {
        EditText editText = findViewById(R.id.edit_text);
        editText.onTextContextMenuItem(android.R.id.undo);
    }


    void onRedoClick(View view) {
        EditText editText = findViewById(R.id.edit_text);
        editText.onTextContextMenuItem(android.R.id.redo);
    }
}
