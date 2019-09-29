package app.haiyunshan.whatsnote.article.insertion;

import android.content.Context;
import android.net.Uri;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.record.entity.ImageEntity;
import club.andnext.glide.SizeDetector;
import club.andnext.utils.ContentUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class PictureInsertion extends BaseInsertion {

    List<Uri> list;

    SizeDetector sizeDetector;

    public PictureInsertion(Callback callback, List<Uri> list) {
        super(callback);

        this.list = list;

        this.sizeDetector = new SizeDetector(callback.getActivity());
    }

    @Override
    ParagraphEntity insert(int position, CharSequence text) {
        Document document = callback.getDocument();

        ImageEntity imageEntity = null;
        ParagraphEntity entity = null;

        int index = position;
        for (int i = 0, size = list.size(); i < size; i++) {
            Uri uri = list.get(i);
            boolean result = sizeDetector.parse(uri);
            if (!result) {
                continue;
            }

            PictureEntity pic = PictureEntity.create(document, uri, sizeDetector.getWidth(), sizeDetector.getHeight());
            if (pic != null) { // copy picture
                this.copyFile(pic, uri);
            }

            if (pic != null) { // create image entry
                imageEntity = ImageEntity.create(pic.getId(), document.getId());
            }

            if (pic != null) { // add target
                document.add(index, pic);
                ++index;

                CharSequence s = ((i + 1) == size)? text: "";
                if (s != null) {
                    ParagraphEntity en = ParagraphEntity.create(document, s);

                    document.add(index, en);
                    ++index;

                    entity = en;
                }
            }
        }

        int count = (index - position);
        if (count > 0) {
            getAdapter().notifyItemRangeInserted(position, count);
        }

        if (imageEntity != null) {
            imageEntity.save();
        }

        return entity;
    }

    void copyFile(final PictureEntity entity, final Uri uri) {
        CompletableFuture.runAsync(() -> {

            Context context = callback.getActivity();

            File file = entity.getFile();
            file.getAbsoluteFile().getParentFile().mkdirs();

            {
                File tmpFile = getTempFile(file);
                tmpFile = ContentUtils.copy(context, uri, tmpFile);
                if (tmpFile != null) {
                    tmpFile.renameTo(file);
                }
            }

        });
    }



}

