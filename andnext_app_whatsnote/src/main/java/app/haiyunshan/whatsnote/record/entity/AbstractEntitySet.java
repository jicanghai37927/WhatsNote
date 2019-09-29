package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import club.andnext.base.BaseEntity;
import club.andnext.base.BaseEntitySet;
import club.andnext.base.BaseEntry;

import java.util.List;

public abstract class AbstractEntitySet<E extends BaseEntity, T extends BaseEntry> extends BaseEntitySet<E, T> {

    public AbstractEntitySet(@NonNull Context context, @Nullable T entry) {
        this(context, entry, null);
    }

    public AbstractEntitySet(@NonNull Context context, @Nullable T entry, @Nullable List<E> list) {
        super(context, entry, list);
    }

    RecordManager getManager() {
        return RecordManager.getInstance(context);
    }
}
