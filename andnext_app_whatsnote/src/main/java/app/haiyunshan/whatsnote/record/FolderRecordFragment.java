package app.haiyunshan.whatsnote.record;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;

import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderRecordFragment extends NamedRecordListFragment {

    RecordEntity data;

    FolderHelper folderHelper;

    public FolderRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder_record, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.folderHelper = new FolderHelper(view);
        }
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = args.getString("id", RecordEntity.ROOT_NOTE);
            this.data = RecordEntity.listAll(id);

            this.replaceAll(data.getList());
        }

        {
            titleBar.setTitle(getTitle(getActivity(), data));
        }

        {
            folderHelper.onActivityCreated();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        {
            this.data = RecordEntity.listAll(data.getId());
            this.replaceAll(data.getList());
        }

        {
            folderHelper.onRestart();
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
    BaseRecordListFragment.Callback createCallback() {
        return new Callback();
    }

    @Override
    void onCreateFolderClick(View view) {
        this.clearSwipe();

        {
            this.requestEdit(data.getId(), "");
        }
    }

    static CharSequence getTitle(Context context, RecordEntity item) {
        CharSequence name = item.getName();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }

        switch (item.getId()) {
            case RecordEntity.ROOT_NOTE: {
                name = "我的笔记";
                break;
            }
            case RecordEntity.ROOT_TRASH: {
                name = "最近删除";
                break;
            }
        }

        return name;
    }

    static boolean exist(RecordEntity entity) {
        if (entity.isRoot()) {
            return true;
        }

        return entity.isPresent();
    }

    static boolean isTrash(RecordEntity entity) {
        return entity.isTrash();
    }

    /**
     *
     */
    private class Callback extends NamedRecordListFragment.Callback {

        @Override
        public RecordEntity onCreateNote(@NonNull String style) {
            RecordEntity entity = RecordEntity.create(data.getId(), getName(style), RecordEntity.TYPE_NOTE, style);

            return entity;
        }

        @Override
        public void onRemove(RecordEntity entity, boolean delete) {

            // remove from sorted list
            remove(entity);

            // remove from data
            data.remove(entity.getId());
        }

        String getName(String style) {
            String name = "空白笔记";
            if (style.equals(RecordEntity.STYLE_CHAT)) {
                name = "新的对话";
            }

            return getName(style, name);
        }

        String getName(String style, String name) {
            Optional<RecordEntity> result = data.findNoteByName(style, name);
            if (!result.isPresent()) {
                return name;
            }

            int count = 2;
            while (true) {
                String text = name + " " + count;
                result = data.findNoteByName(style, text);
                if (!result.isPresent()) {
                    return text;
                }

                ++count;
            }
        }

    }

    /**
     *
     */
    private class FolderHelper {

        View trashView;

        View recordView;
        View errorView;

        FolderHelper(View view) {
            this.trashView = view.findViewById(R.id.tv_trash);

            this.recordView = view.findViewById(R.id.layout_record);
            this.errorView = view.findViewById(R.id.layout_error);
        }

        void onActivityCreated() {
            updateUI();
        }

        void onRestart() {
            updateUI();
        }

        void updateUI() {
            boolean createEnable = true;
            boolean trashVisible = false;
            boolean recordVisible = true;

            RecordEntity entity = data;
            if (exist(entity)) {
                if (isTrash(entity)) {
                    createEnable = false;
                    trashVisible = true;
                    recordVisible = true;
                }
            } else {
                createEnable = false;
                trashVisible = false;
                recordVisible = false;
            }

            setCreateEnable(createEnable);

            trashView.setVisibility(trashVisible? View.VISIBLE: View.GONE);

            recordView.setVisibility(recordVisible? View.VISIBLE: View.INVISIBLE);
            errorView.setVisibility(recordVisible? View.INVISIBLE: View.VISIBLE);
        }

    }
}
