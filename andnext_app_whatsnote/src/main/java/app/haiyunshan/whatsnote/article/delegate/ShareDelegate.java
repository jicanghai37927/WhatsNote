package app.haiyunshan.whatsnote.article.delegate;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.ShareArticleActivity;
import app.haiyunshan.whatsnote.article.helper.ComposeHelper;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;

public class ShareDelegate extends BaseRequestDelegate {

    String articleId;
    ComposeHelper composeHelper;

    public ShareDelegate(Fragment f, ComposeHelper helper) {
        super(f);

        this.articleId = helper.getDocument().getId();
        this.composeHelper = helper;
    }

    public ShareDelegate(Fragment f, String id) {
        super(f);

        this.articleId = id;
        this.composeHelper = null;
    }

    @Override
    public boolean request() {
        String id = this.articleId;
        ShareArticleActivity.startForResult(parent, id, getRequestCode());

        return true;
    }

    @Override
    public void accept(Integer resultCode, Intent data) {

    }
}
