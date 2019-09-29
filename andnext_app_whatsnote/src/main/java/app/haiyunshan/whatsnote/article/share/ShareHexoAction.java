package app.haiyunshan.whatsnote.article.share;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.FormulaEntity;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.TagEntity;
import club.andnext.utils.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ShareHexoAction extends ShareMarkdownAction {

    public ShareHexoAction(Context context, View view, RecordEntity entity, Document document) {
        super(context, view, entity, document);
    }

    @Override
    public String getFooter() {
        String footer = "";

        File file = Directory.get(context, Directory.DIR_FOOTER);
        file = new File(file, "hexo.md");
        if (file.exists()) {
            try {
                footer = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return footer;
    }

    @Override
    void prepare() {

        if (!entity.getPublished().isPresent()) {
            entity.setPublished(DateTime.now());
            entity.save();
        }

        {
            final String path = getFileName(entity);

            this.pictureMap = new HashMap<>();

            document.getList().stream()
                    .filter(e -> e.getClass() == PictureEntity.class)
                    .map(e -> (PictureEntity) e)
                    .forEach(e -> pictureMap.put(e, path + "/" + getName(e)));

            if (pictureMap.size() > 0) {
                File file = new File(targetDir, path);
                file.mkdirs();
            }
        }
    }

    @Override
    CharSequence postCreate(CharSequence text) {
        StringBuilder sb = new StringBuilder(1024 + text.length());

        {
            String frontMatter = this.createFrontMatter(entity);
            sb.append(frontMatter);
        }

        {
            sb.append(super.postCreate(text));
        }

        return sb;
    }

    String createFrontMatter(RecordEntity entity) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("---\n");

        // uuid
        {
            sb.append("uuid: ");
            sb.append(getUUID(entity));
            sb.append('\n');
        }

        // title
        {
            sb.append("title: ");
            sb.append(entity.getName());
            sb.append('\n');
        }

        // categories
        {
            String value = getCategories(entity);
            if (!TextUtils.isEmpty(value)) {
                sb.append("categories: ");
                sb.append(value);
                sb.append('\n');
            }
        }

        // tags
        {
            String value = getTags(entity);
            if (!TextUtils.isEmpty(value)) {
                sb.append("tags: ");
                sb.append(value);
                sb.append('\n');
            }
        }

        // mathjax
        if (hasFormula(document)) {
            sb.append("mathjax: true\n");
        }

        // date
        {
            sb.append("date: ");
            sb.append(getDate(entity));
            sb.append('\n');
        }

        sb.append("---\n\n");


        String text = sb.toString();
        return text;
    }

    static String getUUID(RecordEntity entity) {
        StringBuilder sb = new StringBuilder(entity.getId());

        if (sb.length() == 32) {
            sb.insert(8, '-');
            sb.insert(13, '-');
            sb.insert(18, '-');
            sb.insert(23, '-');
        }

        return sb.toString();
    }

    static String getCategories(RecordEntity entity) {
        List<String> list = new ArrayList<>();

        RecordEntity parent = entity;
        while (parent != null) {
            parent = parent.getParentEntity();
            if (parent == null) {
                break;
            }

            list.add(0, parent.getName());
        }

        return join(list);
    }

    static String getTags(RecordEntity entity) {

        TagEntity tagDs = TagEntity.obtain();
        List<String> array = entity.getTagList();

        List<String> list = array.stream()
                .map(id -> tagDs.get(id))
                .filter(e -> e.isPresent())
                .map(e -> e.get().getName())
                .collect(Collectors.toList());

        return join(list);
    }

    static String join(List<String> list) {

        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(", ");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }

        if (sb.length() > 0) {
            sb.insert(0, '[');
            sb.insert(sb.length(), ']');
        }

        return sb.toString();
    }

    static String getDate(RecordEntity entity) {

        // update publish time
        if (!entity.getPublished().isPresent()) {
            entity.setPublished(DateTime.now());
        }

        DateTime dateTime = entity.getPublished().get(); // use published time
        String text = dateTime.toString("yyyy-MM-dd HH:mm:ss");
        return text;
    }

    static boolean hasFormula(Document document) {
        return document.getList().stream()
                .filter(e -> e.getClass() == FormulaEntity.class)
                .findAny().isPresent();
    }
}
