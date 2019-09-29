package app.haiyunshan.whatsnote.outline.entity;

import app.haiyunshan.whatsnote.article.entity.DocumentEntity;
import club.andnext.utils.UUIDUtils;

public class BaseOutlineEntity<T extends DocumentEntity> {

    String id;

    T parent;

    BaseOutlineEntity(T parent) {
        this.parent = parent;

        this.id = UUIDUtils.next();
    }

    public String getId() {
        return this.id;
    }

    public String getParentId() {
        return parent.getId();
    }

    public T getParent() {
        return parent;
    }

    public void indent(CharSequence prefix) {

    }

    public void delete() {

    }
}
