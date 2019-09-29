package app.haiyunshan.whatsnote.outline.entity;

import android.net.Uri;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class PictureOutlineEntity extends BaseOutlineEntity<PictureEntity> {

    Uri uri;

    public PictureOutlineEntity(PictureEntity parent) {
        super(parent);

        this.uri = parent.getUri();
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public void delete() {
        File file = parent.getFile();
        if (!file.exists()) {
            return;
        }

        CompletableFuture.runAsync(() -> file.delete());
    }
}
