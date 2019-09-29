package app.haiyunshan.whatsnote.article.insertion;

import android.net.Uri;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.record.entity.ImageEntity;
import club.andnext.glide.SizeDetector;

/**
 *
 */
public class CameraInsertion extends BaseInsertion {

    PictureEntity target;

    SizeDetector sizeDetector;

    public CameraInsertion(Callback callback, PictureEntity target) {
        super(callback);

        this.target = target;

        this.sizeDetector = new SizeDetector(callback.getActivity());
    }

    @Override
    ParagraphEntity insert(int position, CharSequence text) {
        Document document = callback.getDocument();

        ImageEntity imageEntity = null;

        ParagraphEntity entity = null;

        int index = position;
        while (true) {
            Uri uri = target.getUri();
            boolean result = sizeDetector.parse(uri);
            if (!result) {
                break;
            }

            PictureEntity pic = this.target;

            {
                pic.setWidth(sizeDetector.getWidth());
                pic.setHeight(sizeDetector.getHeight());
            }

            { // create image entry
                imageEntity = ImageEntity.create(pic.getId(), document.getId());
            }

            if (pic != null) { // add target
                document.add(index, pic);
                ++index;

                CharSequence s = text;
                if (s != null) {
                    ParagraphEntity en = ParagraphEntity.create(document, s);

                    document.add(index, en);
                    ++index;

                    entity = en;
                }
            }

            break;
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

}

