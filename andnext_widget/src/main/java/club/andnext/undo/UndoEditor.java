package club.andnext.undo;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.text.*;
import android.util.Log;
import android.view.DragEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CorrectionInfo;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Helper class used by TextView to handle editable text views.
 *
 *
 */
public class UndoEditor {

    private static final String TAG = "UndoEditor";
    private static final boolean DEBUG_UNDO = false;

    private static final String UNDO_OWNER_TAG = "UndoEditor";

    // Each UndoEditor manages its own undo stack.
    private final UndoManager mUndoManager = new UndoManager();
    private UndoOwner mUndoOwner = mUndoManager.getOwner(UNDO_OWNER_TAG, this);
    final UndoInputFilter mUndoInputFilter = new UndoInputFilter(this);
    boolean mAllowUndo = true;

    private final TextView mTextView;

    public UndoEditor(TextView textView) {
        mTextView = textView;

        {
            InputFilter[] filters = textView.getFilters();
            filters = Arrays.copyOf(filters, filters.length + 1);
            filters[filters.length - 1] = mUndoInputFilter;

            textView.setFilters(filters);
        }
    }

    public ParcelableParcel saveInstanceState() {
        ParcelableParcel state = new ParcelableParcel(getClass().getClassLoader());
        Parcel parcel = state.getParcel();
        mUndoManager.saveInstanceState(parcel);
        mUndoInputFilter.saveInstanceState(parcel);
        return state;
    }

    public void restoreInstanceState(ParcelableParcel state) {
        Parcel parcel = state.getParcel();
        mUndoManager.restoreInstanceState(parcel, state.getClassLoader());
        mUndoInputFilter.restoreInstanceState(parcel);
        // Re-associate this object as the owner of undo state.
        mUndoOwner = mUndoManager.getOwner(UNDO_OWNER_TAG, this);
    }

    public void setHistorySize(int size) {
        mUndoManager.setHistorySize(size);
    }

    public void beginBatchEdit() {
        mUndoInputFilter.beginBatchEdit();
    }

    public void endBatchEdit() {
        mUndoInputFilter.endBatchEdit();
    }

    public void onCommitCorrection(CorrectionInfo info) {
        mUndoInputFilter.freezeLastEdit();
    }

    public void onDropBegin(DragEvent event) {

        mTextView.beginBatchEdit();
        mUndoInputFilter.freezeLastEdit();

    }

    public void onDropEnd(DragEvent event) {

        mTextView.endBatchEdit();
        mUndoInputFilter.freezeLastEdit();
    }

    /**
     * Forgets all undo and redo operations for this UndoEditor.
     */
    public void forgetUndoRedo() {
        UndoOwner[] owners = { mUndoOwner };
        mUndoManager.forgetUndos(owners, -1 /* all */);
        mUndoManager.forgetRedos(owners, -1 /* all */);
    }

    public boolean canUndo() {
        UndoOwner[] owners = { mUndoOwner };
        return mAllowUndo && mUndoManager.countUndos(owners) > 0;
    }

    public boolean canRedo() {
        UndoOwner[] owners = { mUndoOwner };
        return mAllowUndo && mUndoManager.countRedos(owners) > 0;
    }

    public void undo() {
        if (!mAllowUndo) {
            return;
        }
        UndoOwner[] owners = { mUndoOwner };
        mUndoManager.undo(owners, 1);  // Undo 1 action.
    }

    public void redo() {
        if (!mAllowUndo) {
            return;
        }
        UndoOwner[] owners = { mUndoOwner };
        mUndoManager.redo(owners, 1);  // Redo 1 action.
    }

    private static boolean isValidRange(CharSequence text, int start, int end) {
        return 0 <= start && start <= end && end <= text.length();
    }

    /**
     * An InputFilter that monitors text input to maintain undo history. It does not modify the
     * text being typed (and hence always returns null from the filter() method).
     *
     * TODO: Make this span aware.
     */
    public static class UndoInputFilter implements InputFilter {
        private final UndoEditor mEditor;

        // Whether the current filter pass is directly caused by an end-user text edit.
        private boolean mIsUserEdit;

        // Whether the text field is handling an IME composition. Must be parceled in case the user
        // rotates the screen during composition.
        private boolean mHasComposition;

        // Whether the user is expanding or shortening the text
        private boolean mExpanding;

        // Whether the previous edit operation was in the current batch edit.
        private boolean mPreviousOperationWasInSameBatchEdit;

        public UndoInputFilter(UndoEditor editor) {
            mEditor = editor;
        }

        public void saveInstanceState(Parcel parcel) {
            parcel.writeInt(mIsUserEdit ? 1 : 0);
            parcel.writeInt(mHasComposition ? 1 : 0);
            parcel.writeInt(mExpanding ? 1 : 0);
            parcel.writeInt(mPreviousOperationWasInSameBatchEdit ? 1 : 0);
        }

        public void restoreInstanceState(Parcel parcel) {
            mIsUserEdit = parcel.readInt() != 0;
            mHasComposition = parcel.readInt() != 0;
            mExpanding = parcel.readInt() != 0;
            mPreviousOperationWasInSameBatchEdit = parcel.readInt() != 0;
        }

        /**
         * Signals that a user-triggered edit is starting.
         */
        public void beginBatchEdit() {
            if (DEBUG_UNDO) Log.d(TAG, "beginBatchEdit");
            mIsUserEdit = true;
        }

        public void endBatchEdit() {
            if (DEBUG_UNDO) Log.d(TAG, "endBatchEdit");
            mIsUserEdit = false;
            mPreviousOperationWasInSameBatchEdit = false;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (DEBUG_UNDO) {
                Log.d(TAG, "filter: source=" + source + " (" + start + "-" + end + ") "
                        + "dest=" + dest + " (" + dstart + "-" + dend + ")");
            }

            // Check to see if this edit should be tracked for undo.
            if (!canUndoEdit(source, start, end, dest, dstart, dend)) {
                return null;
            }

            final boolean hadComposition = mHasComposition;
            mHasComposition = isComposition(source);
            final boolean wasExpanding = mExpanding;
            boolean shouldCreateSeparateState = false;
            if ((end - start) != (dend - dstart)) {
                mExpanding = (end - start) > (dend - dstart);
                if (hadComposition && mExpanding != wasExpanding) {
                    shouldCreateSeparateState = true;
                }
            }

            // Handle edit.
            handleEdit(source, start, end, dest, dstart, dend, shouldCreateSeparateState);
            return null;
        }

        void freezeLastEdit() {
            mEditor.mUndoManager.beginUpdate("Edit text");
            EditOperation lastEdit = getLastEdit();
            if (lastEdit != null) {
                lastEdit.mFrozen = true;
            }
            mEditor.mUndoManager.endUpdate();
        }

        private @interface MergeMode {}
        private static final int MERGE_EDIT_MODE_FORCE_MERGE = 0;
        private static final int MERGE_EDIT_MODE_NEVER_MERGE = 1;
        /** Use {@link EditOperation#mergeWith} to merge */
        private static final int MERGE_EDIT_MODE_NORMAL = 2;

        private void handleEdit(CharSequence source, int start, int end,
                                Spanned dest, int dstart, int dend, boolean shouldCreateSeparateState) {
            // An application may install a TextWatcher to provide additional modifications after
            // the initial input filters run (e.g. a credit card formatter that adds spaces to a
            // string). This results in multiple filter() calls for what the user considers to be
            // a single operation. Always undo the whole set of changes in one step.
            @MergeMode
            final int mergeMode;
            if (isInTextWatcher() || mPreviousOperationWasInSameBatchEdit) {
                mergeMode = MERGE_EDIT_MODE_FORCE_MERGE;
            } else if (shouldCreateSeparateState) {
                mergeMode = MERGE_EDIT_MODE_NEVER_MERGE;
            } else {
                mergeMode = MERGE_EDIT_MODE_NORMAL;
            }
            // Build a new operation with all the information from this edit.
            String newText = TextUtils.substring(source, start, end);
            String oldText = TextUtils.substring(dest, dstart, dend);
            EditOperation edit = new EditOperation(mEditor, oldText, dstart, newText,
                    mHasComposition);
            if (mHasComposition && TextUtils.equals(edit.mNewText, edit.mOldText)) {
                return;
            }
            recordEdit(edit, mergeMode);
        }

        private EditOperation getLastEdit() {
            final UndoManager um = mEditor.mUndoManager;
            return um.getLastOperation(
                    EditOperation.class, mEditor.mUndoOwner, UndoManager.MERGE_MODE_UNIQUE);
        }
        /**
         * Fetches the last undo operation and checks to see if a new edit should be merged into it.
         * If forceMerge is true then the new edit is always merged.
         */
        private void recordEdit(EditOperation edit, @MergeMode int mergeMode) {
            // Fetch the last edit operation and attempt to merge in the new edit.
            final UndoManager um = mEditor.mUndoManager;
            um.beginUpdate("Edit text");
            EditOperation lastEdit = getLastEdit();
            if (lastEdit == null) {
                // Add this as the first edit.
                if (DEBUG_UNDO) Log.d(TAG, "filter: adding first op " + edit);
                um.addOperation(edit, UndoManager.MERGE_MODE_NONE);
            } else if (mergeMode == MERGE_EDIT_MODE_FORCE_MERGE) {
                // Forced merges take priority because they could be the result of a non-user-edit
                // change and this case should not create a new undo operation.
                if (DEBUG_UNDO) Log.d(TAG, "filter: force merge " + edit);
                lastEdit.forceMergeWith(edit);
            } else if (!mIsUserEdit) {
                // An application directly modified the Editable outside of a text edit. Treat this
                // as a new change and don't attempt to merge.
                if (DEBUG_UNDO) Log.d(TAG, "non-user edit, new op " + edit);
                um.commitState(mEditor.mUndoOwner);
                um.addOperation(edit, UndoManager.MERGE_MODE_NONE);
            } else if (mergeMode == MERGE_EDIT_MODE_NORMAL && lastEdit.mergeWith(edit)) {
                // Merge succeeded, nothing else to do.
                if (DEBUG_UNDO) Log.d(TAG, "filter: merge succeeded, created " + lastEdit);
            } else {
                // Could not merge with the last edit, so commit the last edit and add this edit.
                if (DEBUG_UNDO) Log.d(TAG, "filter: merge failed, adding " + edit);
                um.commitState(mEditor.mUndoOwner);
                um.addOperation(edit, UndoManager.MERGE_MODE_NONE);
            }
            mPreviousOperationWasInSameBatchEdit = mIsUserEdit;
            um.endUpdate();
        }

        private boolean canUndoEdit(CharSequence source, int start, int end,
                                    Spanned dest, int dstart, int dend) {
            if (!mEditor.mAllowUndo) {
                if (DEBUG_UNDO) Log.d(TAG, "filter: undo is disabled");
                return false;
            }

            if (mEditor.mUndoManager.isInUndo()) {
                if (DEBUG_UNDO) Log.d(TAG, "filter: skipping, currently performing undo/redo");
                return false;
            }

            // Text filters run before input operations are applied. However, some input operations
            // are invalid and will throw exceptions when applied. This is common in tests. Don't
            // attempt to undo invalid operations.
            if (!isValidRange(source, start, end) || !isValidRange(dest, dstart, dend)) {
                if (DEBUG_UNDO) Log.d(TAG, "filter: invalid op");
                return false;
            }

            // Earlier filters can rewrite input to be a no-op, for example due to a length limit
            // on an input field. Skip no-op changes.
            if (start == end && dstart == dend) {
                if (DEBUG_UNDO) Log.d(TAG, "filter: skipping no-op");
                return false;
            }

            return true;
        }

        private static boolean isComposition(CharSequence source) {
            if (!(source instanceof Spannable)) {
                return false;
            }
            // This is a composition edit if the source has a non-zero-length composing span.
            Spannable text = (Spannable) source;
            int composeBegin = BaseInputConnection.getComposingSpanStart(text);
            int composeEnd = BaseInputConnection.getComposingSpanEnd(text);
            return composeBegin < composeEnd;
        }

        private boolean isInTextWatcher() {
            CharSequence text = mEditor.mTextView.getText();
            return (text instanceof SpannableStringBuilder)
                    && ((SpannableStringBuilder) text).getTextWatcherDepth() > 0;
        }
    }

    /**
     * An operation to undo a single "edit" to a text view.
     */
    public static class EditOperation extends UndoOperation<UndoEditor> {
        private static final int TYPE_INSERT = 0;
        private static final int TYPE_DELETE = 1;
        private static final int TYPE_REPLACE = 2;

        private int mType;
        private String mOldText;
        private String mNewText;
        private int mStart;

        private int mOldCursorPos;
        private int mNewCursorPos;
        private boolean mFrozen;
        private boolean mIsComposition;

        /**
         * Constructs an edit operation from a text input operation on editor that replaces the
         * oldText starting at dstart with newText.
         */
        public EditOperation(UndoEditor editor, String oldText, int dstart, String newText,
                             boolean isComposition) {
            super(editor.mUndoOwner);
            mOldText = oldText;
            mNewText = newText;

            // Determine the type of the edit.
            if (mNewText.length() > 0 && mOldText.length() == 0) {
                mType = TYPE_INSERT;
            } else if (mNewText.length() == 0 && mOldText.length() > 0) {
                mType = TYPE_DELETE;
            } else {
                mType = TYPE_REPLACE;
            }

            mStart = dstart;
            // Store cursor data.
            mOldCursorPos = editor.mTextView.getSelectionStart();
            mNewCursorPos = dstart + mNewText.length();
            mIsComposition = isComposition;
        }

        public EditOperation(Parcel src, ClassLoader loader) {
            super(src, loader);
            mType = src.readInt();
            mOldText = src.readString();
            mNewText = src.readString();
            mStart = src.readInt();
            mOldCursorPos = src.readInt();
            mNewCursorPos = src.readInt();
            mFrozen = src.readInt() == 1;
            mIsComposition = src.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mType);
            dest.writeString(mOldText);
            dest.writeString(mNewText);
            dest.writeInt(mStart);
            dest.writeInt(mOldCursorPos);
            dest.writeInt(mNewCursorPos);
            dest.writeInt(mFrozen ? 1 : 0);
            dest.writeInt(mIsComposition ? 1 : 0);
        }

        private int getNewTextEnd() {
            return mStart + mNewText.length();
        }

        private int getOldTextEnd() {
            return mStart + mOldText.length();
        }

        @Override
        public void commit() {
        }

        @Override
        public void undo() {
            if (DEBUG_UNDO) Log.d(TAG, "undo");
            // Remove the new text and insert the old.
            UndoEditor editor = getOwnerData();
            Editable text = (Editable) editor.mTextView.getText();
            modifyText(text, mStart, getNewTextEnd(), mOldText, mStart, mOldCursorPos);
        }

        @Override
        public void redo() {
            if (DEBUG_UNDO) Log.d(TAG, "redo");
            // Remove the old text and insert the new.
            UndoEditor editor = getOwnerData();
            Editable text = (Editable) editor.mTextView.getText();
            modifyText(text, mStart, getOldTextEnd(), mNewText, mStart, mNewCursorPos);
        }

        /**
         * Attempts to merge this existing operation with a new edit.
         * @param edit The new edit operation.
         * @return If the merge succeeded, returns true. Otherwise returns false and leaves this
         * object unchanged.
         */
        private boolean mergeWith(EditOperation edit) {
            if (DEBUG_UNDO) {
                Log.d(TAG, "mergeWith old " + this);
                Log.d(TAG, "mergeWith new " + edit);
            }

            if (mFrozen) {
                return false;
            }

            switch (mType) {
                case TYPE_INSERT:
                    return mergeInsertWith(edit);
                case TYPE_DELETE:
                    return mergeDeleteWith(edit);
                case TYPE_REPLACE:
                    return mergeReplaceWith(edit);
                default:
                    return false;
            }
        }

        private boolean mergeInsertWith(EditOperation edit) {
            if (edit.mType == TYPE_INSERT) {
                // Merge insertions that are contiguous even when it's frozen.
                if (getNewTextEnd() != edit.mStart) {
                    return false;
                }
                mNewText += edit.mNewText;
                mNewCursorPos = edit.mNewCursorPos;
                mFrozen = edit.mFrozen;
                mIsComposition = edit.mIsComposition;
                return true;
            }
            if (mIsComposition && edit.mType == TYPE_REPLACE
                    && mStart <= edit.mStart && getNewTextEnd() >= edit.getOldTextEnd()) {
                // Merge insertion with replace as they can be single insertion.
                mNewText = mNewText.substring(0, edit.mStart - mStart) + edit.mNewText
                        + mNewText.substring(edit.getOldTextEnd() - mStart, mNewText.length());
                mNewCursorPos = edit.mNewCursorPos;
                mIsComposition = edit.mIsComposition;
                return true;
            }
            return false;
        }

        // TODO: Support forward delete.
        private boolean mergeDeleteWith(EditOperation edit) {
            // Only merge continuous deletes.
            if (edit.mType != TYPE_DELETE) {
                return false;
            }
            // Only merge deletions that are contiguous.
            if (mStart != edit.getOldTextEnd()) {
                return false;
            }
            mStart = edit.mStart;
            mOldText = edit.mOldText + mOldText;
            mNewCursorPos = edit.mNewCursorPos;
            mIsComposition = edit.mIsComposition;
            return true;
        }

        private boolean mergeReplaceWith(EditOperation edit) {
            if (edit.mType == TYPE_INSERT && getNewTextEnd() == edit.mStart) {
                // Merge with adjacent insert.
                mNewText += edit.mNewText;
                mNewCursorPos = edit.mNewCursorPos;
                return true;
            }
            if (!mIsComposition) {
                return false;
            }
            if (edit.mType == TYPE_DELETE && mStart <= edit.mStart
                    && getNewTextEnd() >= edit.getOldTextEnd()) {
                // Merge with delete as they can be single operation.
                mNewText = mNewText.substring(0, edit.mStart - mStart)
                        + mNewText.substring(edit.getOldTextEnd() - mStart, mNewText.length());
                if (mNewText.isEmpty()) {
                    mType = TYPE_DELETE;
                }
                mNewCursorPos = edit.mNewCursorPos;
                mIsComposition = edit.mIsComposition;
                return true;
            }
            if (edit.mType == TYPE_REPLACE && mStart == edit.mStart
                    && TextUtils.equals(mNewText, edit.mOldText)) {
                // Merge with the replace that replaces the same region.
                mNewText = edit.mNewText;
                mNewCursorPos = edit.mNewCursorPos;
                mIsComposition = edit.mIsComposition;
                return true;
            }
            return false;
        }

        /**
         * Forcibly creates a single merged edit operation by simulating the entire text
         * contents being replaced.
         */
        public void forceMergeWith(EditOperation edit) {
            if (DEBUG_UNDO) Log.d(TAG, "forceMerge");
            if (mergeWith(edit)) {
                return;
            }
            UndoEditor editor = getOwnerData();

            // Copy the text of the current field.
            // NOTE: Using StringBuilder instead of SpannableStringBuilder would be somewhat faster,
            // but would require two parallel implementations of modifyText() because Editable and
            // StringBuilder do not share an interface for replace/delete/insert.
            Editable editable = (Editable) editor.mTextView.getText();
            Editable originalText = new SpannableStringBuilder(editable.toString());

            // Roll back the last operation.
            modifyText(originalText, mStart, getNewTextEnd(), mOldText, mStart, mOldCursorPos);

            // Clone the text again and apply the new operation.
            Editable finalText = new SpannableStringBuilder(editable.toString());
            modifyText(finalText, edit.mStart, edit.getOldTextEnd(),
                    edit.mNewText, edit.mStart, edit.mNewCursorPos);

            // Convert this operation into a replace operation.
            mType = TYPE_REPLACE;
            mNewText = finalText.toString();
            mOldText = originalText.toString();
            mStart = 0;
            mNewCursorPos = edit.mNewCursorPos;
            mIsComposition = edit.mIsComposition;
            // mOldCursorPos is unchanged.
        }

        private static void modifyText(Editable text, int deleteFrom, int deleteTo,
                                       CharSequence newText, int newTextInsertAt, int newCursorPos) {
            // Apply the edit if it is still valid.
            if (isValidRange(text, deleteFrom, deleteTo)
                    && newTextInsertAt <= text.length() - (deleteTo - deleteFrom)) {
                if (deleteFrom != deleteTo) {
                    text.delete(deleteFrom, deleteTo);
                }
                if (newText.length() != 0) {
                    text.insert(newTextInsertAt, newText);
                }
            }
            // Restore the cursor position. If there wasn't an old cursor (newCursorPos == -1) then
            // don't explicitly set it and rely on SpannableStringBuilder to position it.
            // TODO: Select all the text that was undone.
            if (0 <= newCursorPos && newCursorPos <= text.length()) {
                Selection.setSelection(text, newCursorPos);
            }
        }

        private String getTypeString() {
            switch (mType) {
                case TYPE_INSERT:
                    return "insert";
                case TYPE_DELETE:
                    return "delete";
                case TYPE_REPLACE:
                    return "replace";
                default:
                    return "";
            }
        }

        @Override
        public String toString() {
            return "[mType=" + getTypeString() + ", "
                    + "mOldText=" + mOldText + ", "
                    + "mNewText=" + mNewText + ", "
                    + "mStart=" + mStart + ", "
                    + "mOldCursorPos=" + mOldCursorPos + ", "
                    + "mNewCursorPos=" + mNewCursorPos + ", "
                    + "mFrozen=" + mFrozen + ", "
                    + "mIsComposition=" + mIsComposition + "]";
        }

        public static final Parcelable.ClassLoaderCreator<EditOperation> CREATOR =
                new Parcelable.ClassLoaderCreator<EditOperation>() {
                    @Override
                    public EditOperation createFromParcel(Parcel in) {
                        return new EditOperation(in, null);
                    }

                    @Override
                    public EditOperation createFromParcel(Parcel in, ClassLoader loader) {
                        return new EditOperation(in, loader);
                    }

                    @Override
                    public EditOperation[] newArray(int size) {
                        return new EditOperation[size];
                    }
                };
    }

}

