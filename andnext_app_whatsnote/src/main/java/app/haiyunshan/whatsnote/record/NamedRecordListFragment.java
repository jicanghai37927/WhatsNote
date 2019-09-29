package app.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
public abstract class NamedRecordListFragment extends BaseRecordListFragment {

    View createFolderBtn;
    TextView sortBtn;

    public NamedRecordListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_named_record_list, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.createFolderBtn = view.findViewById(R.id.btn_create_folder);
            createFolderBtn.setOnClickListener(this::onCreateFolderClick);

            this.sortBtn = view.findViewById(R.id.tv_sort);
            sortBtn.setOnClickListener(this::onSortClick);
        }

        {
            createFolderBtn.setVisibility(getCallback().isCreateEnable()? View.VISIBLE: View.INVISIBLE);
        }

    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.updateSortText(this.sortEntity);
        }
    }

    @Override
    void setCreateEnable(boolean enable) {
        super.setCreateEnable(enable);

        createFolderBtn.setVisibility(enable? View.VISIBLE: View.INVISIBLE);
    }

    @Override
    void setSort(SortEntity entity) {
        super.setSort(entity);


        SortEntity sort = this.getSort();

        // save to option
        OptionEntity.obtain().setSort(sort);
        OptionEntity.obtain().save();

        // update
        updateSortText(sort);
    }

    void onCreateFolderClick(View view) {

    }

    void onSortClick(View view) {
        this.clearSwipe();

        {
            SortHelper helper = new SortHelper();
            helper.request();
        }
    }

    void updateSortText(SortEntity entity) {
        String text = String.format("已按%1$s排序", entity.getName());
        sortBtn.setText(text);
    }

    /**
     *
     */
    abstract class Callback extends BaseRecordListFragment.Callback {

        @Override
        public SortEntity getSortEntity() {
            return OptionEntity.obtain().getSort();
        }
    }

    /**
     *
     */
    private class SortHelper {

        void request() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            {
                builder.setTitle("排序方式：");
            }

            {
                final SortEntity entity = SortEntity.listAll();
                String[] items = new String[entity.size()];
                for (int i = 0, size = items.length; i < size; i++) {
                    items[i] = entity.get(i).getName();
                }

                builder.setItems(items, (dialog, which) ->
                    accept(entity.get(which))
                );
            }

            {
                builder.setPositiveButton(android.R.string.yes, null);
            }

            builder.show();
        }

        void accept(SortEntity entity) {
            setSort(entity);
        }

    }
}
