package app.haiyunshan.whatsnote.article.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import club.andnext.undo.UndoEditor;
import club.andnext.widget.ParagraphEdit;

public class UndoHelper implements LifecycleObserver,
        ViewTreeObserver.OnGlobalFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        TextWatcher {

    View decorView;

    View undoBtn;
    View redoBtn;

    Fragment parent;

    public UndoHelper(Fragment f, View undoBtn, View redoBtn) {
        this.parent = f;
        f.getLifecycle().addObserver(this);

        this.undoBtn = undoBtn;
        undoBtn.setEnabled(false);
        undoBtn.setOnClickListener(this::onUndoClick);

        this.redoBtn = redoBtn;
        redoBtn.setEnabled(false);
        redoBtn.setOnClickListener(this::onRedoClick);

        this.decorView = f.getActivity().getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        decorView.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        this.updateButtons(newFocus);

        if (oldFocus != null && oldFocus instanceof ParagraphEdit) {
            ParagraphEdit focus = (ParagraphEdit)oldFocus;
            focus.removeTextChangedListener(this);
        }

        if (newFocus != null && newFocus instanceof ParagraphEdit) {
            ParagraphEdit focus = (ParagraphEdit)newFocus;
            focus.removeTextChangedListener(this);
            focus.addTextChangedListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        this.updateButtons();
    }

    public void updateButtons() {
        View focus = parent.getActivity().getCurrentFocus();
        this.updateButtons(focus);
    }

    void onUndoClick(View view) {
        View focus = parent.getActivity().getCurrentFocus();
        this.undo(focus);
    }

    void onRedoClick(View view) {
        View focus = parent.getActivity().getCurrentFocus();
        this.redo(focus);
    }

    void updateButtons(View view) {

        boolean canUndo = false;
        boolean canRedo = false;

        if (view != null && (view instanceof ParagraphEdit)) {
            ParagraphEdit edit = (ParagraphEdit)view;
            UndoEditor e = edit.getUndoEditor();
            canUndo = e.canUndo();
            canRedo = e.canRedo();
        }

        undoBtn.setEnabled(canUndo);
        redoBtn.setEnabled(canRedo);
    }

    void undo(View view) {
        if (view != null && (view instanceof ParagraphEdit)) {
            ParagraphEdit edit = (ParagraphEdit)view;
            UndoEditor e = edit.getUndoEditor();
            if (e.canUndo()) {
                e.undo();
            }
        }

        this.updateButtons(view);
    }

    void redo(View view) {
        if (view != null && (view instanceof ParagraphEdit)) {
            ParagraphEdit edit = (ParagraphEdit)view;
            UndoEditor e = edit.getUndoEditor();
            if (e.canRedo()) {
                e.redo();
            }

        }

        this.updateButtons(view);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateButtons();
    }
}
