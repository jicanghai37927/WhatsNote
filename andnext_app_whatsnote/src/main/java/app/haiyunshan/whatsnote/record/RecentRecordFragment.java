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
import app.haiyunshan.whatsnote.record.entity.RecentRecordSet;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import org.joda.time.DateTime;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentRecordFragment extends TabbedRecordListFragment {

    RecentRecordSet data;

    public RecentRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_record, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.data = RecentRecordSet.create("");
            this.replaceAll(data.getList());
        }

        {
            titleBar.setTitle("最近项目");
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        {
            this.data = RecentRecordSet.create("");
            this.replaceAll(data.getList());
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
        return new Callback();
    }

    /**
     *
     */
    class Callback extends TabbedRecordListFragment.Callback {

        @Override
        public DateTime getTime(RecordEntity entity) {
            return entity.getModified();
        }

        @Override
        public void onRemove(RecordEntity entity, boolean delete) {

            // remove from sorted list
            remove(entity);

            // remove from data
            data.remove(entity.getId());
        }

    }
}
