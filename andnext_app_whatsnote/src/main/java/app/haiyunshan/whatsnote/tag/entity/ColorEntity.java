package app.haiyunshan.whatsnote.tag.entity;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.tag.database.ColorTable;
import app.haiyunshan.whatsnote.tag.entry.ColorEntry;
import club.andnext.base.BaseEntitySet;
import club.andnext.utils.ColorUtils;
import club.andnext.utils.GsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ColorEntity extends BaseEntitySet<ColorEntity, ColorEntry> {

    public static final ColorEntity TRANSPARENT = new ColorEntity(null, null);

    private static ColorEntity instance;

    int icon = Color.TRANSPARENT;
    int color = 0xff474747;
    int check = 0xff606060;
    int background = 0xffe0e0e0;

    ColorEntity(@NonNull Context context, @Nullable ColorEntry entry) {
        this(context, entry, null);
    }

    ColorEntity(@NonNull Context context, @Nullable ColorEntry entry, @Nullable List<ColorEntity> list) {
        super(context, entry, list);

        if (entry != null) {
            this.icon = ColorUtils.parse(entry.getIcon());
            this.color = ColorUtils.parse(entry.getColor());
            this.check = ColorUtils.parse(entry.getCheck());
            this.background = ColorUtils.parse(entry.getBackground());
        }
    }

    public int getIcon() {
        return icon;
    }

    public int getColor() {
        return color;
    }

    public int getCheck() {
        return check;
    }

    public int getBackground() {
        return background;
    }

    public static ColorEntity obtain() {
        if (instance == null) {
            Context context = WhatsApp.getInstance();
            instance = Factory.create(context);
        }

        return instance;
    }

    private static class Factory {

        static ColorEntity create(Context context) {
            ColorTable table = GsonUtils.fromJson(context, "record/color_ds.json", ColorTable.class);
            if (table == null) {
                return new ColorEntity(context, null, Collections.emptyList());
            }

            List<ColorEntity> list = table.getList().stream()
                    .map(entry -> new ColorEntity(context, entry))
                    .collect(Collectors.toList());

            return new ColorEntity(context, null, list);
        }
    }
}
