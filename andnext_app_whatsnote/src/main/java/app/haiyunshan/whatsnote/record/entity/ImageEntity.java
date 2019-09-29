package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.record.database.ImageTable;
import app.haiyunshan.whatsnote.record.entry.ImageEntry;

import java.util.List;

public class ImageEntity extends AbstractEntitySet<ImageEntity, ImageEntry> {

    ImageEntity(Context context, ImageEntry entry) {
        super(context, entry);
    }

    public CharSequence getName() {
        return String.format("IMG_%04d", entry.getNumber());
    }

    public int getNumber() {
        return entry.getNumber();
    }

    public ImageEntity delete() {
        ImageTable ds = RecordManager.getInstance(context).obtainTable(ImageTable.class);
        ds.remove(entry);

        return this;
    }

    public long save() {
        return getManager().save(ImageTable.class);
    }

    public static final ImageEntity obtain(String id, String documentId) {
        return Factory.obtain(WhatsApp.getInstance(), id, documentId);
    }

    public static final ImageEntity create(String id, String documentId) {
        return Factory.create(WhatsApp.getInstance(), id, documentId);
    }

    public static final long deleteByDocument(String documentId) {
        return Factory.deleteByDocument(WhatsApp.getInstance(), documentId);
    }

    /**
     *
     */
    static class Factory {

        static final ImageEntity create(Context context, String id, String documentId) {
            ImageTable ds = RecordManager.getInstance(context).obtainTable(ImageTable.class);

            ImageEntry entry = ds.get(id, documentId).orElse(new ImageEntry(id, ds.increase(), documentId));
            ds.add(entry);

            return new ImageEntity(context, entry);
        }

        static final ImageEntity obtain(Context context, String id, String documentId) {
            ImageTable ds = RecordManager.getInstance(context).obtainTable(ImageTable.class);

            ImageEntry entry = ds.get(id, documentId).orElse(new ImageEntry(id, 0, ""));

            return new ImageEntity(context, entry);
        }

        static final long deleteByDocument(Context context, String documentId) {
            ImageTable ds = RecordManager.getInstance(context).obtainTable(ImageTable.class);

            return ds.removeByDocument(documentId);
        }

    }

}
