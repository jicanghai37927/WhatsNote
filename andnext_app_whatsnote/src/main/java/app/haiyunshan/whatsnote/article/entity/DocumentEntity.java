package app.haiyunshan.whatsnote.article.entity;

import android.content.Context;
import app.haiyunshan.whatsnote.article.entry.Article;
import app.haiyunshan.whatsnote.article.entry.ArticleEntry;

public abstract class DocumentEntity<T extends ArticleEntry> {

    protected T entry;
    protected Document parent;

    protected DocumentEntity(Document d, T entry) {
        this.parent = d;
        this.entry = entry;
    }

    public String getId() {
        return entry.getId();
    }

    protected abstract void save();

    public T getEntry() {
        return entry;
    }

    public Article getArticle() {
        return getDocument().getArticle();
    }

    public Document getDocument() {
        return parent;
    }

    public Context getContext() {
        return getDocument().context;
    }

    DocumentManager getManager() {
        return parent.getManager();
    }
}
