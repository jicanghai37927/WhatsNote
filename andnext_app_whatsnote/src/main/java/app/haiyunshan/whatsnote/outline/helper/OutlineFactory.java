package app.haiyunshan.whatsnote.outline.helper;

import android.text.SpannableStringBuilder;
import app.haiyunshan.whatsnote.article.entity.*;
import app.haiyunshan.whatsnote.outline.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class OutlineFactory {

    public Document from(Outline provider) {
        List<BaseOutlineEntity> array = provider.getList();

        Document document = new Document(provider.getDocument().getContext(), provider.getDocument().getId());

        // transform
        List<BaseOutlineEntity> list = new ArrayList<>();
        for (BaseOutlineEntity entity : array) {
            if (list.isEmpty()) {
                list.add(entity);
            } else {
                BaseOutlineEntity last = list.get(list.size() - 1);
                if (last.getClass() != (entity.getClass())) {
                     append(document, list);
                     list.clear();
                }

                list.add(entity);
            }
        }

        // append last
        if (!list.isEmpty()) {
            append(document, list);
            list.clear();
        }

        // make sure has an entity
        if (document.isEmpty()) {
            document.add(ParagraphEntity.create(document, ""));
        }

        // make sure first & last entities are paragraph
        {
            DocumentEntity entity = document.get(document.size() - 1);
            if (entity.getClass() != ParagraphEntity.class) {
                document.add(ParagraphEntity.create(document, ""));
            }

            entity = document.get(0);
            if (entity.getClass() != ParagraphEntity.class) {
                document.add(0, ParagraphEntity.create(document, ""));
            }
        }

        return document;
    }

    public Outline from(Document document) {

        HashMap<Class<? extends DocumentEntity>,
                Function<DocumentEntity, List<? extends BaseOutlineEntity>>> map;
        {
            map = new HashMap<>();

            // paragraph
            map.put(ParagraphEntity.class, (e) -> {
                ParagraphEntity entity = (ParagraphEntity)e;

                ArrayList<ParagraphOutlineEntity> list = new ArrayList<>();
                CharSequence text = entity.getText();
                while (true) {
                    int pos = indexOf(text, '\n');
                    if (pos >= 0) {
                        list.add(new ParagraphOutlineEntity(entity, text.subSequence(0, pos)));
                        text = text.subSequence(pos + 1, text.length());
                    } else {
                        list.add(new ParagraphOutlineEntity(entity, text));
                        break;
                    }
                }

                return list;
            });

            // picture
            map.put(PictureEntity.class, (e) -> {
                PictureEntity entity = (PictureEntity)e;

                ArrayList<PictureOutlineEntity> list = new ArrayList<>();
                list.add(new PictureOutlineEntity(entity));

                return list;
            });

            // formula
            map.put(FormulaEntity.class, (e) -> {
                FormulaEntity entity = (FormulaEntity)e;

                ArrayList<FormulaOutlineEntity> list = new ArrayList<>();
                list.add(new FormulaOutlineEntity(entity));

                return list;
            });
        }

        ArrayList<BaseOutlineEntity> list = new ArrayList<>(7 * document.size());
        {
            List<DocumentEntity> array = document.getList();
            for (DocumentEntity e : array) {
                Function<DocumentEntity, List<? extends BaseOutlineEntity>> f = map.get(e.getClass());
                if (f == null) {
                    continue;
                }

                List<? extends BaseOutlineEntity> result = f.apply(e);
                if (result != null) {
                    list.addAll(result);
                }
            }
        }

        return new Outline(document, list);
    }

    static void append(Document document, List<BaseOutlineEntity> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        Class clz = list.get(0).getClass();
        if (clz == ParagraphOutlineEntity.class) { // paragraph

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            for (BaseOutlineEntity e: list) {
                ParagraphOutlineEntity entity = (ParagraphOutlineEntity)e;
                ssb.append(entity.getContent());
                ssb.append('\n');
            }

            if (ssb.length() > 0) {
                ssb.delete(ssb.length() - 1, ssb.length());
            }

            document.add(ParagraphEntity.create(document, ssb));;

        } else if (clz == PictureOutlineEntity.class) { // picture
            for (BaseOutlineEntity e : list) {
                PictureOutlineEntity entity = (PictureOutlineEntity)e;
                document.add(entity.getParent());
            }
        } else if (clz == FormulaOutlineEntity.class) { // formula
            for (BaseOutlineEntity e : list) {
                FormulaOutlineEntity entity = (FormulaOutlineEntity)e;
                document.add(entity.getParent());
            }
        }

    }

    static int indexOf(CharSequence text, char c) {
        for (int i = 0, size = text.length(); i < size; i++) {
            if (text.charAt(i) == c) {
                return i;
            }
        }

        return -1;
    }
}
