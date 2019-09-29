package club.andnext.base;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BaseTable<T extends BaseEntry> {

    @SerializedName("list")
    List<T> list;

    public BaseTable() {

    }

    public Optional<T> get(final String id) {
        return getList().stream()
                .filter(e -> e.getId().equals(id))
                .findAny();
    }

    public T get(int index) {
        return getList().get(index);
    }

    public int size() {
        return getList().size();
    }

    public void add(T entry) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(entry);
    }

    public void add(int index, T entry) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(index, entry);
    }

    public void remove(T entry) {
        if (list == null) {
            return;
        }

        list.remove(entry);
    }

    public void remove(String id) {
        if (list == null) {
            return;
        }

        this.get(id).ifPresent(e -> list.remove(e));
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
    }

    public List<T> getList() {
        return (list == null)? Collections.emptyList(): list;
    }
}
