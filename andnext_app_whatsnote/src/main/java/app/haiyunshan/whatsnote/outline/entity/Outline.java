package app.haiyunshan.whatsnote.outline.entity;

import app.haiyunshan.whatsnote.article.entity.Document;
import club.andnext.utils.ListUtils;

import java.util.List;

public class Outline {

    List<BaseOutlineEntity> list;

    Document document;

    public Outline(Document document, List<BaseOutlineEntity> list) {
        this.document = document;

        this.list = list;
    }

    public BaseOutlineEntity get(int position) {
        return list.get(position);
    }

    public int size() {
        return list.size();
    }

    public Document getDocument() {
        return this.document;
    }

    public List<BaseOutlineEntity> getList() {
        return list;
    }

    public void move(Object from, Object to) {
        int a = list.indexOf(from);
        int b = list.indexOf(to);

        ListUtils.move(list, a, b);
    }

    public int indexOf(BaseOutlineEntity entity) {
        return list.indexOf(entity);
    }

    public void remove(BaseOutlineEntity entity) {
        list.remove(entity);
    }

    public void add(int index, BaseOutlineEntity entity) {
        list.add(index, entity);
    }
}