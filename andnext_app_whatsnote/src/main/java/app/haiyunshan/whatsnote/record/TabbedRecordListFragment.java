package app.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.widget.RadioButton;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.entity.OptionEntity;
import app.haiyunshan.whatsnote.record.entity.SortEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TabbedRecordListFragment extends BaseRecordListFragment {

    RadioButton nameSortBtn;
    RadioButton modifiedSortBtn;

    public TabbedRecordListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tabbed_record_list, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            nameSortBtn = view.findViewById(R.id.rb_name);
            nameSortBtn.setOnClickListener(this::onNameSortClick);

            modifiedSortBtn = view.findViewById(R.id.rb_modified);
            modifiedSortBtn.setOnClickListener(this::onModifiedSortClick);
        }
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            SortEntity sort = this.getSort();
            if (sort.getId().equals(SortEntity.ID_NAME)) {
                nameSortBtn.setChecked(true);
            }
            if (sort.getId().equals(SortEntity.ID_MODIFIED)) {
                modifiedSortBtn.setChecked(true);
            }
        }
    }

    @Override
    void setSort(SortEntity entity) {
        super.setSort(entity);

        // save to option
        SortEntity sort = this.getSort();
        OptionEntity.obtain().setRecentSort(sort);

        OptionEntity.obtain().save();
    }

    void onNameSortClick(View view) {
        this.clearSwipe();

        {
            SortEntity sort = this.getSort();
            if (!sort.getId().equals(SortEntity.ID_NAME)) {
                sort = SortEntity.create(SortEntity.ID_NAME);
            }

            this.setSort(sort);
        }
    }

    void onModifiedSortClick(View view) {
        this.clearSwipe();

        {
            SortEntity sort = this.getSort();
            if (!sort.getId().equals(SortEntity.ID_MODIFIED)) {
                sort = SortEntity.create(SortEntity.ID_MODIFIED);
                sort.setReverse(true);
            }

            this.setSort(sort);
        }
    }



    /**
     *
     */
    abstract class Callback extends BaseRecordListFragment.Callback {

        @Override
        public boolean isCreateEnable() {
            return false;
        }

        @Override
        public SortEntity getSortEntity() {
            return OptionEntity.obtain().getRecentSort();
        }
    }
}
