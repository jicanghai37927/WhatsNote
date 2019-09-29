package app.haiyunshan.whatsnote.tag;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.TagEntity;
import app.haiyunshan.whatsnote.tag.entity.ColorEntity;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.utils.SoftInputUtils;
import club.andnext.widget.CircleColorButton;
import club.andnext.widget.CircleColorView;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseComposeTagFragment extends Fragment {

    TitleBar titleBar;

    View editLayout;
    CircleColorView iconView;
    EditText editText;

    LinearLayout colorLayout;

    CircleColorButton target;
    HashMap<ColorEntity, CircleColorButton> colorMap;

    public BaseComposeTagFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_compose_tag, container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        {
            this.titleBar = view.findViewById(R.id.title_bar);
            titleBar.setNegativeListener(this::onCancleClick);
        }

        {
            this.editLayout = view.findViewById(R.id.edit_layout);
            editLayout.setOnClickListener(this::onEditClick);
        }

        {
            this.iconView = view.findViewById(R.id.iv_icon);
        }

        {
            this.editText = view.findViewById(R.id.edit_text);
            ClearAssistMenuHelper.attach(editText);
        }

        {
            this.colorLayout = view.findViewById(R.id.color_layout);

            this.colorMap = new HashMap<>();

            ColorEntity ds = ColorEntity.obtain();
            List<ColorEntity> list = ds.getList();
            for (ColorEntity entity : list) {
                CircleColorButton btn = (CircleColorButton)(getLayoutInflater().inflate(R.layout.layout_tag_color_button, colorLayout, false));
                btn.setOnClickListener(this::onColorClick);
                btn.setColor(entity.getIcon());
                btn.setTag(entity);

                colorLayout.addView(btn);

                colorMap.put(entity, btn);
            }
        }
    }

    void onCancleClick(View view) {
        getActivity().onBackPressed();
    }

    void onEditClick(View view) {
        editText.requestFocus();
        SoftInputUtils.show(getActivity(), editText);
    }

    void onColorClick(View view) {
        this.setTarget((ColorEntity)(view.getTag()));
    }

    void setTarget(ColorEntity entity) {
        if (target != null) {
            target.setChecked(false);
            target = null;
        }

        if (entity == null) {
            return;
        }

        this.target = colorMap.get(entity);
        if (target == null) {
            return;
        }

        target.setChecked(true);

        iconView.setColor(target.getColor());
    }

    ColorEntity getTarget() {
        if (target == null) {
            return null;
        }

        return (ColorEntity)(target.getTag());
    }
}
