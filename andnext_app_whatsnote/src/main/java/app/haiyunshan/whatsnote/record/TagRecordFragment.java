package app.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.TagRecordSet;
import club.andnext.widget.CircleColorView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagRecordFragment extends NamedRecordListFragment {

    CircleColorView iconView;

    TagRecordSet data;

    public TagRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_record, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int resource = R.layout.layout_tag_icon;
        ViewGroup layout = titleBar.getTitleLayout();
        this.iconView = (CircleColorView)getLayoutInflater().inflate(resource, layout, false);
        layout.addView(iconView, 0);
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = args.getString("id");

            this.data = TagRecordSet.create(id);
            this.replaceAll(data.getList());
        }

        {
            titleBar.setTitle(data.getName());
            iconView.setColor(data.getColor());
        }

    }


    @Override
    public void onRestart() {
        super.onRestart();

        this.data = TagRecordSet.create(data.getId());
        this.replaceAll(data.getList());
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

    /**
     *
     */
    class Callback extends NamedRecordListFragment.Callback {

        @Override
        public void onRemove(RecordEntity entity, boolean delete) {

            // remove from sorted list
            remove(entity);

            // remove from data
            data.remove(entity.getId());
        }
    }
}
