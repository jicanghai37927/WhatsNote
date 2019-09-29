package club.andnext.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class BaseEntity<T extends BaseEntry> {

    protected T entry;

    protected Context context;

    public BaseEntity(@NonNull Context context) {
        this(context, null);
    }

    public BaseEntity(@NonNull Context context, @Nullable T entry) {
        this.entry = entry;

        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public BaseEntity(BaseEntity<T> another) {
        this(another.context, another.entry);
    }

    public String getId() {
        if (entry == null) {
            return "";
        }

        return entry.getId();
    }

    public Context getContext() {
        return this.context;
    }

    public boolean isPresent() {
        return entry != null;
    }

    public Optional<T> getEntry() {
        return Optional.ofNullable(entry);
    }

    public boolean areContentsTheSame(BaseEntity obj) {
        return this.equals(obj);
    }
}
