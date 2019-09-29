package app.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.SearchRecordSet;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.utils.SoftInputUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRecordFragment extends NamedRecordListFragment {

    EditText editKeyword;
    View cancelBtn;
    View emptyView;

    SearchRecordSet data;

    public SearchRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.editKeyword = view.findViewById(R.id.edit_keyword);
        editKeyword.addTextChangedListener(new KeywordWatcher());

        this.cancelBtn = view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this::onNavigationClick);

        this.emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            recyclerView.addOnScrollListener(new SearchScrollListener());
        }

        {
            ClearAssistMenuHelper.attach(editKeyword);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        if (data != null) {
            data = SearchRecordSet.create(data.getKeyword());
            replaceAll(data.getList());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (data != null) {
            data.save();
        }
    }

    @Override
    Callback createCallback() {
        Callback callback = new Callback();
        callback.createEnable = false;

        return callback;
    }

    void requestSearch(String keyword) {
        this.clearSwipe();

        {
            boolean isEmpty = TextUtils.isEmpty(keyword);

            if (isEmpty) {
                appBarLayout.setExpanded(false, false);
            }

            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);
            emptyView.setClickable(!isEmpty);

            data = SearchRecordSet.create(keyword);
            replaceAll(data.getList());

            recyclerView.scrollToPosition(0);
        }

    }

    /**
     *
     */
    private class KeywordWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            requestSearch(s.toString());
        }
    }

    /**
     *
     */
    private class SearchScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            SoftInputUtils.hide(getActivity());
        }
    }


    /**
     *
     */
    class Callback extends NamedRecordListFragment.Callback {

        @Override
        public void onRemove(RecordEntity entity, boolean delete) {

            // only when delete
            if (delete) {

                // remove from sorted list
                remove(entity);

                // remove from data
                data.remove(entity.getId());
            } else {
                requestSearch(editKeyword.getText().toString());
            }
        }
    }
}
