package app.haiyunshan.whatsandroid;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.undo.UndoEditor;
import club.andnext.utils.TextViewUtils;
import club.andnext.widget.ParagraphEdit;
import club.andnext.helper.ClearAssistMenuHelper;

public class TestUndoActivity extends AppCompatActivity {

    ParagraphEdit editText;
    View undoBtn;
    View redoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_test_undo);

        undoBtn = findViewById(R.id.btn_undo);
        redoBtn = findViewById(R.id.btn_redo);

        editText = findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUndo();
            }
        });

        {
            updateUndo();
        }

        {
            ClearAssistMenuHelper helper = ClearAssistMenuHelper.attach(editText);
            helper.setArguments(android.R.id.undo, android.R.id.redo);
        }

        editText.post(() -> {
            TextViewUtils.undo(editText);
            TextViewUtils.canUndo(editText);

            TextViewUtils.redo(editText);
            TextViewUtils.canRedo(editText);

            TextViewUtils.forgetUndoRedo(editText);
        });
    }

    void onUndoClick(View view) {
        editText.getUndoEditor().undo();
        updateUndo();
    }

    void onRedoClick(View view) {
        editText.getUndoEditor().redo();
        updateUndo();
    }

    void onSpanClick(View view) {

    }

    void updateUndo() {
        UndoEditor editor = editText.getUndoEditor();
        undoBtn.setEnabled(editor.canUndo());
        redoBtn.setEnabled(editor.canRedo());
    }

}
