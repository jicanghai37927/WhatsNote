package app.haiyunshan.whatsnote.tag;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.entity.TagEntity;
import app.haiyunshan.whatsnote.tag.entity.ColorEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTagFragment extends BaseComposeTagFragment {

    static String sLastColor = "transparent";

    public CreateTagFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            titleBar.setTitle("添加新标签");
            titleBar.setPositiveEnable(false);
            titleBar.setPositiveListener(this::onDoneClick);
        }

        {
            editText.addTextChangedListener(new TagWatcher());
        }

        if (!TextUtils.isEmpty(sLastColor)) {
            ColorEntity entity = ColorEntity.obtain().get(sLastColor).orElse(null);
            this.setTarget(entity);
        }
    }

    void onDoneClick(View view) {
        ColorEntity color = this.getTarget();
        if (color == null) {
            return;
        }

        String name = editText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            return;
        }

        if (TagEntity.obtain().indexOfName(name) >= 0) {
            return;
        }

        TagEntity entity = TagEntity.obtain().add(name, color.getId());
        if (entity == null) {
            return;
        }

        {
            TagEntity.obtain().save();
        }

        {
            Intent intent = new Intent();
            intent.putExtra("id", entity.getId());
            getActivity().setResult(Activity.RESULT_OK, intent);

            getActivity().onBackPressed();
        }
    }

    @Override
    void setTarget(ColorEntity entity) {
        super.setTarget(entity);

        sLastColor = (entity == null)? "": entity.getId();
    }

    /**
     *
     */
    private class TagWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = s.toString().trim();
            boolean enable;

            if (TextUtils.isEmpty(name)) {
                enable = false;
            } else {
                int index = TagEntity.obtain().indexOfName(name);
                enable = (index < 0);
            }

            titleBar.setPositiveEnable(enable);
        }
    }
}
