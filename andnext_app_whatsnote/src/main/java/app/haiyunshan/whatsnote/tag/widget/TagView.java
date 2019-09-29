package app.haiyunshan.whatsnote.tag.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.TagEntity;
import app.haiyunshan.whatsnote.tag.entity.ColorEntity;
import club.andnext.widget.CircleColorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagView extends FrameLayout {

    List<CircleColorView> list;

    ArrayList<ColorEntity> colorList;

    public TagView(@NonNull Context context) {
        this(context, null);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {
            int resource = R.layout.merge_tag;
            LayoutInflater.from(context).inflate(resource, this, true);
        }

        {
            this.list = Arrays.asList(
                    findViewById(R.id.iv_start),
                    findViewById(R.id.iv_middle),
                    findViewById(R.id.iv_end));

            for (CircleColorView view : list) {
                view.setVisibility(View.INVISIBLE);
            }
        }

        {
            this.colorList = new ArrayList<>();
        }
    }

    public void setTarget(RecordEntity target) {

        colorList.clear();

        {
            TagEntity tagDs = TagEntity.obtain();
            ColorEntity colorDs = ColorEntity.obtain();

            List<String> tagList = target.getTagList();

            for (String id : tagList) {
                TagEntity tag = tagDs.get(id).orElse(null);
                if (tag == null) {
                    continue;
                }

                if (!tag.getEntry().isPresent()) {
                    continue;
                }

                ColorEntity color = colorDs.get(tag.getEntry().get().getColor()).orElse(null);
                if (color == null) {
                    continue;
                }

                colorList.add(color);
            }
        }

        {
            int index = colorList.size() - 1;

            for (int i = list.size() - 1; i >= 0; i--) {
                CircleColorView view = list.get(i);

                if (index < 0) {
                    view.setVisibility(View.INVISIBLE);
                    continue;
                }

                view.setVisibility(View.VISIBLE);
                view.setColor(colorList.get(index).getIcon());

                --index;
            }
        }

        colorList.clear();
    }
}
