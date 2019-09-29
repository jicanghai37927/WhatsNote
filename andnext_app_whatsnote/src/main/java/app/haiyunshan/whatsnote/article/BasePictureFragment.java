package app.haiyunshan.whatsnote.article;


import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BasePictureFragment extends Fragment {

    GalleryProvider provider;
    String documentId;
    String pictureId;

    public BasePictureFragment() {

    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            this.documentId = args.getString("documentId");
            this.pictureId = args.getString("pictureId");
        }

        {
            Document d = Document.create(documentId, true);

            this.provider = new GalleryProvider(d);
        }
    }

    /**
     *
     */
    class GalleryProvider implements ClazzAdapterProvider<PictureEntity> {

        List<PictureEntity> list;

        GalleryProvider(Document document) {
            this.list = document.getList().stream()
                    .filter(e -> (e.getClass() == PictureEntity.class))
                    .map(e -> (PictureEntity)e)
                    .collect(Collectors.toList());
        }

        public int indexOf(final String id) {
            return list.stream()
                    .filter(e -> e.getId().equals(id))
                    .mapToInt(e -> list.indexOf(e))
                    .findAny().orElse(-1);
        }

        @Override
        public PictureEntity get(int position) {
            return list.get(position);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

}
