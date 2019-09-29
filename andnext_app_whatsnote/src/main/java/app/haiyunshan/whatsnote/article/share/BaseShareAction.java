package app.haiyunshan.whatsnote.article.share;

import android.content.Context;
import android.view.View;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;

public abstract class BaseShareAction {

    Context context;
    View view;

    protected RecordEntity entity;
    protected Document document;

    public BaseShareAction(Context context, View view, RecordEntity entity, Document document) {
        this.context = context;
        this.view = view;

        this.entity = entity;
        this.document = document;
    }

    public abstract void execute();

    public abstract String getFooter();
}
