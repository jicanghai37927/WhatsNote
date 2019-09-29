package app.haiyunshan.whatsnote.article.entity;

import android.content.Context;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.article.entry.Article;
import club.andnext.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Document {

    static final String TAG = "Document";

    String id;
    Article article;

    List<DocumentEntity> list;

    boolean readOnly = false;

    Context context;

    Document(Context context, String id, Article article) {
        this.context = context.getApplicationContext();

        this.id = id;
        this.article = article;

        final DocumentManager.Factory factory = getManager().getFactory();
        this.list = article.getList().stream()
                .map(e -> factory.create(Document.this, e))
                .collect(Collectors.toList());
    }

    public Document(Context context, String id) {
        this.context = context.getApplicationContext();

        this.id = id;
        this.article = new Article();

        this.list = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }

    public String getId() {
        return this.id;
    }

    public File getFile() {
        return getManager().getDir(this.id);
    }

    public String getTitle() {
        return getList().stream().filter(e -> e.getClass() == ParagraphEntity.class)
                .map(e -> (ParagraphEntity)e)
                .filter(e -> e.getText().length() != 0)
                .map(ParagraphEntity::getText)
                .map(e -> Utils.getTitle(e, 32))
                .findFirst().orElse("");
    }

    public DocumentEntity get(int index) {
        return list.get(index);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public List<DocumentEntity> getList() {
        return list;
    }

    public void add(DocumentEntity entity) {
        list.add(entity);
    }

    public void add(int index, DocumentEntity entity) {
        list.add(index, entity);
    }

    public int remove(DocumentEntity entity) {
        int index = list.indexOf(entity);
        if (index < 0) {
            return index;
        }

        list.remove(index);

        return index;
    }

    public int remove(int index) {
        if (index < 0 || index >= list.size()) {
            return -1;
        }

        list.remove(index);
        return index;
    }

    public int indexOf(DocumentEntity entity) {
        return list.indexOf(entity);
    }

    public int indexOf(String id) {
        for (int i = 0, size = list.size(); i < size; i++) {
            DocumentEntity en = list.get(i);
            if (en.getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public void cache() {
        if (readOnly) {
            return;
        }

        for (DocumentEntity e : list) {
            e.save();
        }

        article.clear();
        for (DocumentEntity entity : list) {
            article.add(entity.getEntry());
        }

        getManager().cache(this);
    }

    public long save() {
        if (readOnly) {
            return 0;
        }

        return getManager().save(this);
    }

    public Document setReadOnly(boolean value) {
        this.readOnly = value;

        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    DocumentManager getManager() {
        return DocumentManager.getInstance(context);
    }

    Article getArticle() {
        return article;
    }

    public static Document create(String id) {
        return create(id, false);
    }

    public static Document create(String id, boolean readOnly) {
        return Factory.create(WhatsApp.getInstance(), id).setReadOnly(readOnly);
    }

    public static File getFile(Context context, String id) {
        return DocumentManager.getDir(context, id);
    }

    /**
     *
     *
     */
    private static class Factory {

        static Document create(Context context, String id) {
            return DocumentManager.getInstance(context).create(id, "");
        }

    }
}
