package app.haiyunshan.whatsnote.article.entity;

import android.net.Uri;
import app.haiyunshan.whatsnote.article.entry.PictureEntry;
import club.andnext.utils.UUIDUtils;

import java.io.File;

public class PictureEntity extends ParagraphEntity<PictureEntry> {

    Uri uri;

    public PictureEntity(Document d, PictureEntry entry) {
        super(d, entry);
    }

    @Override
    protected void save() {
        super.save();
    }

    public int getWidth() {
        return entry.getWidth();
    }

    public PictureEntity setWidth(int width) {
        entry.setWidth(width);

        return this;
    }

    public int getHeight() {
        return entry.getHeight();
    }

    public PictureEntity setHeight(int height) {
        entry.setHeight(height);

        return this;
    }

    public Uri getUri() {
        if (uri != null) {
            return uri;
        }

        File file = this.getFile();
        if ((file != null) && file.exists()) {
            uri = Uri.fromFile(file);
        } else {
            uri = Uri.parse(getEntry().getUri());
        }

        return uri;
    }

    public File getFile() {
        return getManager().getFile(getDocument().getId(), DocumentManager.TYPE_PICTURE, this.getId() + ".pic");
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder();

        sb.append("picture://"); // article id
        sb.append(getDocument().getId());

        sb.append('/'); // entry id
        sb.append(this.getEntry().getId());

        sb.append("?hash="); // picture hash
        sb.append(this.getEntry().getSignature());

        return sb.toString();
    }

    void setUri(Uri uri) {
        this.uri = uri;
        entry.setUri(uri);
    }

    public static final PictureEntity create(Document d) {
        PictureEntity entity = create(d, null, 0, 0);
        Uri uri = Uri.fromFile(entity.getFile());
        entity.setUri(uri);

        return entity;
    }

    public static final PictureEntity create(Document d, Uri uri, int width, int height) {

        String id = UUIDUtils.next();
        PictureEntry entry = new PictureEntry(id, uri, width, height);

        PictureEntity entity = new PictureEntity(d, entry);
        entity.uri = uri;

        return entity;
    }
}
