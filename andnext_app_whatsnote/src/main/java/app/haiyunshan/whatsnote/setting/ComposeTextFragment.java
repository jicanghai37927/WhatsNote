package app.haiyunshan.whatsnote.setting;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.ucrop.BuildConfig;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeTextFragment extends Fragment {

    private static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;

    SearchTitleBar titleBar;

    TextView nameView;
    EditText editText;
    TextView explainView;

    String text;
    MenuItem doneMenuItem;

    public ComposeTextFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);

            Toolbar toolbar = titleBar.getToolbar();
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(this::onNavigationClick);

            toolbar.inflateMenu(R.menu.menu_compose_text);
            toolbar.setOnMenuItemClickListener(new MenuItemClickListener().put(R.id.menu_done, this::onDoneClick));

            this.doneMenuItem = toolbar.getMenu().findItem(R.id.menu_done);
            doneMenuItem.setEnabled(false);
        }

        {
            this.nameView = view.findViewById(R.id.tv_name);
            this.editText = view.findViewById(R.id.edit_text);
            this.explainView = view.findViewById(R.id.tv_explain);
        }

        {
            ClearAssistMenuHelper.attach(editText);

            this.editText.addTextChangedListener(new ComposeTextListener());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = this.getArguments();

        {
            CharSequence cs = args.getCharSequence(Builder.EXTRA_TEXT);

            this.text = (TextUtils.isEmpty(cs))? "": cs.toString();
        }

        {
            CharSequence title = args.getCharSequence(Builder.EXTRA_TITLE);
            titleBar.setTitle(title);
        }

        {
            int maxLength = args.getInt(Options.EXTRA_MAX_LENGTH, -1);
            if (maxLength > 0) {
                InputFilter[] filters = editText.getFilters();

                {
                    filters = Arrays.copyOf(filters, filters.length + 1);
                    filters[filters.length - 1] = new InputFilter.LengthFilter(maxLength);
                }

                editText.setFilters(filters);
            }

            int minLines = args.getInt(Options.EXTRA_MIN_LINES, -1);
            if (minLines > 0) {
                editText.setMinLines(minLines);
            }

            int maxLines = args.getInt(Options.EXTRA_MAX_LINES, -1);
            if (maxLines > 0) {
                editText.setMaxLines(maxLines);
            }

            if (minLines <= 1 && maxLines <= 1) {
                editText.setSingleLine(true);
            }

        }

        {
            CharSequence hint = args.getCharSequence(Options.EXTRA_HINT);
            editText.setHint(hint);
        }

        {
            CharSequence text = args.getCharSequence(Options.EXTRA_NAME);
            nameView.setVisibility(TextUtils.isEmpty(text)? View.GONE: View.VISIBLE);
            nameView.setText(text);
        }

        {
            CharSequence text = args.getCharSequence(Options.EXTRA_EXPLAIN);
            explainView.setVisibility(TextUtils.isEmpty(text)? View.GONE: View.VISIBLE);
            explainView.setText(text);
        }

        {
            editText.setText(text);
            editText.setSelection(text.length());
        }

    }

    void onNavigationClick(View view) {
        getActivity().onBackPressed();
    }

    void onDoneClick(int itemId) {
        Intent intent = new Intent();
        intent.putExtra(Builder.EXTRA_TEXT, editText.getText().toString());

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
    }

    /**
     *
     */
    private class ComposeTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean equals = (text.equals(s.toString()));
            doneMenuItem.setEnabled(!equals);
        }
    }

    /**
     *
     */
    public static class Builder {

        public static final String EXTRA_TITLE  = EXTRA_PREFIX + ".Title";
        public static final String EXTRA_TEXT   = EXTRA_PREFIX + ".Text";

        protected Intent intent;
        protected Bundle bundle;

        public Builder(@NonNull CharSequence title, @NonNull CharSequence text) {
            this.intent = new Intent();

            this.bundle = new Bundle();
            bundle.putCharSequence(EXTRA_TITLE, title);
            bundle.putCharSequence(EXTRA_TEXT, text);
        }

        public Builder withOptions(@NonNull Options options) {
            bundle.putAll(options.getOptionBundle());

            return this;
        }
    }

    /**
     *
     */
    public static class Options {

        public static final String EXTRA_NAME = EXTRA_PREFIX + ".Name";
        public static final String EXTRA_HINT = EXTRA_PREFIX + ".Hint";
        public static final String EXTRA_EXPLAIN = EXTRA_PREFIX + ".Explain";

        public static final String EXTRA_MAX_LENGTH = EXTRA_PREFIX + ".MaxLength";

        public static final String EXTRA_MIN_LINES = EXTRA_PREFIX + ".MinLines";
        public static final String EXTRA_MAX_LINES = EXTRA_PREFIX + ".MaxLines";

        private final Bundle mOptionBundle;

        public Options() {
            mOptionBundle = new Bundle();
        }

        @NonNull
        public Bundle getOptionBundle() {
            return mOptionBundle;
        }

        public void setName(CharSequence name) {
            mOptionBundle.putCharSequence(EXTRA_NAME, name);
        }

        public void setHint(CharSequence hint) {
            mOptionBundle.putCharSequence(EXTRA_HINT, hint);
        }

        public void setExplain(CharSequence explain) {
            mOptionBundle.putCharSequence(EXTRA_EXPLAIN, explain);
        }

        public void setMaxLength(int length) {
            mOptionBundle.putInt(EXTRA_MAX_LENGTH, length);
        }

        public void setMinLines(int value) {
            mOptionBundle.putInt(EXTRA_MIN_LINES, value);
        }

        public void setMaxLines(int value) {
            mOptionBundle.putInt(EXTRA_MAX_LINES, value);
        }

    }
}
