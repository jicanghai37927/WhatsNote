package app.haiyunshan.whatsnote.article.viewholder;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.helper.SoftInputHelper;
import club.andnext.recyclerview.bridge.BridgeViewHolder;

public abstract class ComposeViewHolder<E extends DocumentEntity> extends BridgeViewHolder<E> implements SoftInputHelper.OnSoftInputListener {

    protected E entity;

    protected Callback callback;

    private TextChangeListener textChangeListener;

    @Keep
    public ComposeViewHolder(Callback callback, View itemView) {
        super(itemView);

        this.callback = callback;
    }

    public String getId() {
        return getItem().getId();
    }

    public void setTextChangeListener(TextChangeListener listener) {
        this.textChangeListener = listener;
    }

    @Override
    public abstract int getLayoutResourceId();

    @Override
    public abstract void onViewCreated(@NonNull View view);

    @Override
    @CallSuper
    public void onBind(E item, int position) {
        this.entity = item;
    }

    @Override
    @CallSuper
    public void onViewAttachedToWindow() {
        if (textChangeListener != null) {
            EditText editText = textChangeListener.view;

            editText.removeTextChangedListener(textChangeListener);
            editText.addTextChangedListener(textChangeListener);
        }
    }

    @Override
    @CallSuper
    public void onViewDetachedFromWindow() {
        if (textChangeListener != null) {
            EditText editText = textChangeListener.view;

            editText.removeTextChangedListener(textChangeListener);
        }

        {
            save();
        }
    }

    @Override
    public void onSoftInputChanged(SoftInputHelper helper, boolean visible) {

    }

    void requestRemove() {
        callback.remove(this);
    }

    void requestView() {
        callback.view(this);
    }

    public abstract void save();

    public E getEntity() {
        return entity;
    }

    /**
     *
     */
    public static abstract class Callback {

        protected boolean enable;

        protected Activity context;

        public Callback(Activity context) {
            this.context = context;

            this.enable = true;
        }

        public Activity getContext() {
            return this.context;
        }

        public boolean isEnable() {
            return this.enable;
        }

        public int getMaxWidth() {
            return getContext().getResources().getDisplayMetrics().widthPixels;
        }

        public int getMaxHeight() {
            return getContext().getResources().getDisplayMetrics().heightPixels;
        }

        public abstract void remove(ComposeViewHolder viewHolder);

        public abstract void view(ComposeViewHolder viewHolder);

        public abstract void compose(ComposeViewHolder viewHolder);

        public void afterTextChanged(ComposeViewHolder viewHolder, View view) {}

        public void requestSave(ComposeViewHolder viewHolder) {}

    }


    /**
     *
     */
    static class TextChangeListener implements TextWatcher {

        ComposeViewHolder parent;
        EditText view;

        TextChangeListener(ComposeViewHolder holder, EditText view) {
            this.parent = holder;
            this.view = view;

            {
                ClearAssistMenuHelper helper = ClearAssistMenuHelper.attach(view);

                // we don't need undo/redo also.
                helper.setArguments(android.R.id.undo, android.R.id.redo);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            parent.callback.afterTextChanged(parent, view);
        }
    }
}
