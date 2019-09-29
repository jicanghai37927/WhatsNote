package club.andnext.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CorrectionInfo;
import androidx.appcompat.widget.AppCompatEditText;
import club.andnext.undo.ParcelableParcel;
import club.andnext.undo.UndoEditor;

public class ParagraphEdit extends AppCompatEditText {

    UndoEditor undoEditor;

    public ParagraphEdit(Context context) {
        this(context, null);
    }

    public ParagraphEdit(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ParagraphEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.undoEditor = new UndoEditor(this);
        undoEditor.setHistorySize(100);
    }

    public UndoEditor getUndoEditor() {
        return undoEditor;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        if (undoEditor != null) {
            ss.editorState = undoEditor.saveInstanceState();
        }

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.editorState != null) {

            if (undoEditor == null) {
                undoEditor = new UndoEditor(this);
            }

            undoEditor.restoreInstanceState(ss.editorState);
        }
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        // we don't need it any more
        menu.removeItem(android.R.id.undo);
        menu.removeItem(android.R.id.redo);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if (event.hasModifiers(KeyEvent.META_CTRL_ON)) {
            // Handle Ctrl-only shortcuts.
            switch (keyCode) {
                case KeyEvent.KEYCODE_Z: {
                    if (undoEditor.canUndo()) {
                        return onTextContextMenuItem(android.R.id.undo);
                    }
                    break;
                }

            }
        } else if (event.hasModifiers(KeyEvent.META_CTRL_ON | KeyEvent.META_SHIFT_ON)) {

            // Handle Ctrl-Shift shortcuts.
            switch (keyCode) {
                case KeyEvent.KEYCODE_Z: {
                    if (undoEditor.canRedo()) {
                        return onTextContextMenuItem(android.R.id.redo);
                    }
                    break;
                }
            }
        }

        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            undoEditor.onDropBegin(event);
        }

        boolean result = super.onDragEvent(event);

        if (event.getAction() == DragEvent.ACTION_DROP) {
            undoEditor.onDropEnd(event);
        }

        return result;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        if (undoEditor != null) {
            undoEditor.forgetUndoRedo();
        }

    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        if (id == android.R.id.paste) {
            id = android.R.id.pasteAsPlainText;
        }

        boolean result = false;

        switch (id) {
            case android.R.id.undo: {
                result = true;
                undoEditor.undo();
                break;
            }
            case android.R.id.redo: {
                result = true;
                undoEditor.redo();
                break;
            }
        }

        if (result) {
            return true;
        }

        return super.onTextContextMenuItem(id);
    }

    @Override
    public void onBeginBatchEdit() {
        super.onBeginBatchEdit();

        undoEditor.beginBatchEdit();

    }

    @Override
    public void onEndBatchEdit() {
        super.onEndBatchEdit();

        undoEditor.endBatchEdit();
    }

    @Override
    public void onCommitCorrection(CorrectionInfo info) {
        super.onCommitCorrection(info);

        undoEditor.onCommitCorrection(info);
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState {

        ParcelableParcel editorState;  // Optional state from Editor.

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            if (editorState == null) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                editorState.writeToParcel(out, flags);
            }
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<ParagraphEdit.SavedState> CREATOR =
                new Parcelable.Creator<ParagraphEdit.SavedState>() {

                    public ParagraphEdit.SavedState createFromParcel(Parcel in) {
                        return new ParagraphEdit.SavedState(in);
                    }

                    public ParagraphEdit.SavedState[] newArray(int size) {
                        return new ParagraphEdit.SavedState[size];
                    }

                };

        private SavedState(Parcel in) {
            super(in);

            if (in.readInt() != 0) {
                editorState = ParcelableParcel.CREATOR.createFromParcel(in);
            }
        }
    }

}
