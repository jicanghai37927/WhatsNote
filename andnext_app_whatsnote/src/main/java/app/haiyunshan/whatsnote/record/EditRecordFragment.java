package app.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
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
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.widget.RecordIconView;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.utils.AlertDialogUtils;

import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditRecordFragment extends Fragment {

    TitleBar titleBar;
    RecordIconView iconView;
    EditText editName;

    RecordEntity data;
    Optional<RecordEntity> item;

    public EditRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            titleBar.setNegativeButton("取消", this::onNegativeClick);
            titleBar.setPositiveButton("完成", this::onPositiveClick);

            this.iconView = view.findViewById(R.id.iv_icon);
            this.editName = view.findViewById(R.id.edit_name);
            editName.addTextChangedListener(new EditWatcher());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            this.data = RecordEntity.listAll(args.getString("parentId", RecordEntity.ROOT_NOTE));
            this.item = data.get(args.getString("id", ""));
        }

        if (item.isPresent()) {
            iconView.setIcon(item.get());
        }

        {
            ClearAssistMenuHelper.attach(editName);
        }

        {
            CharSequence text = "新建文件夹";

            if (item.isPresent()) {

                if (item.get().isDirectory()) {
                    text = "给文件夹重新命名";
                } else {
                    text = "重新命名笔记";
                }
            }

            titleBar.setTitle(text);
        }

        {
            int start = 0;
            CharSequence text = "未命名文件夹";

            if (item.isPresent()) {
                text = item.get().getName();
                start = text.length();
            }

            editName.setText(text);
            editName.setSelection(start, text.length());
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        data.save();
    }

    void onNegativeClick(View view) {
        getActivity().onBackPressed();
    }

    void onPositiveClick(View view) {
        String name = editName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) { // name is empty
            return;
        }

        if (!item.isPresent() || !item.get().getName().equals(name)) {

            Optional<RecordEntity> target = null;
            if (!item.isPresent() || item.get().isDirectory()) { // if not present, it must be directory
                target = data.findDirectoryByName(name);
            } else { // note, chat or article or else
                target = data.findNoteByName(item.get().getStyle(), name);
            }

            if (target.isPresent()) { // name is exist
                String text = String.format("名称“%1$s”已被占用。请选取其他名称。", name);
                AlertDialogUtils.showMessage(getContext(), text, null);
                return;
            }

        }

        RecordEntity entity;
        if (item.isPresent()) {
            entity = item.get();
        } else {
            entity = RecordEntity.create(data.getId(), name, RecordEntity.TYPE_FOLDER, null);
            data.add(entity);
        }

        {
            entity.setAlias("");
            entity.setName(name);
        }

        {
            Intent intent = new Intent();
            intent.putExtra("id", entity.getId());
            getActivity().setResult(Activity.RESULT_OK, intent);
        }

        {
            getActivity().onBackPressed();
        }
    }

    /**
     *
     */
    private class EditWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString().trim();
            titleBar.setPositiveEnable(!text.isEmpty());
        }
    }
}
