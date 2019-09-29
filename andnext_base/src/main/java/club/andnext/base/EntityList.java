package club.andnext.base;

import club.andnext.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EntityList {

    List<BaseEntity> list;

    public EntityList(List<? extends BaseEntity> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = new ArrayList<>(list);
        }
    }

    public boolean move(final String fromId, final String toId) {
        int from = this.indexOf(fromId);
        int to = this.indexOf(toId);

        if (from < 0 || to < 0) {
            return false;
        }

        ListUtils.move(list, from, to);
        return true;
    }

    public BaseEntity get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public void add(int index, BaseEntity entity) {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(index, entity);
    }

    public BaseEntity remove(final String id) {
        BaseEntity e = null;

        int index = indexOf(id);
        if (index >= 0) {
            e = list.remove(index);
        }

        return e;
    }

    public int indexOf(final String id) {

        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public List<BaseEntity> getList() {
        return list;
    }
}
