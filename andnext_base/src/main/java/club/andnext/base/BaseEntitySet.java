package club.andnext.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BaseEntitySet<E extends BaseEntity, T extends BaseEntry> extends BaseEntity<T> {

    protected List<E> list;

    public BaseEntitySet(@NonNull Context context, @Nullable T entry) {
        this(context, entry, null);
    }

    public BaseEntitySet(@NonNull Context context, @Nullable T entry, @Nullable List<E> list) {
        super(context, entry);

        if (list != null) {
            this.list = new ArrayList<>(list);
        }
    }

    public Optional<E> get(final String id) {
        return getList().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public E get(int index) {
        return getList().get(index);
    }

    public int size() {
        return getList().size();
    }

    public boolean isEmpty() {
        return getList().isEmpty();
    }

    public final void add(E entity) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(entity);
    }

    public final void add(int index, E entity) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(index, entity);
    }

    public final E remove(final String id) {
        E e = null;

        int index = indexOf(id);
        if (index >= 0) {
            e = list.remove(index);
        }

        return e;
    }

    public int indexOf(final String id) {
        final List<E> list = getList();

        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public List<E> getList() {
        return (list == null)? Collections.emptyList(): list;
    }

    public EntityList toList() {
        return new EntityList(this.list);
    }
}
