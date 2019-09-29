package app.haiyunshan.whatsnote.article.share;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.*;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import club.andnext.utils.AssetUtils;
import club.andnext.utils.FileUtils;
import club.andnext.utils.UriUtils;
import club.andnext.xsltml.MathMLTransformer;
import com.google.android.material.snackbar.Snackbar;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ShareMarkdownAction extends BaseShareFileAction {

    HashMap<PictureEntity, String> pictureMap;

    public ShareMarkdownAction(Context context, View view, RecordEntity entity, Document document) {
        super(context, view, entity, document);

        {
            String name = context.getString(R.string.share_directory);
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
            dir = new File(dir, "Markdown");
            dir = new File(dir, getFileName(entity));
            dir.mkdirs();

            this.targetDir = dir;
        }
    }

    @Override
    void accept() {
        Observable.just(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onNext);
    }

    @Override
    public String getFooter() {
        String footer = "";

        File file = Directory.get(context, Directory.DIR_FOOTER);
        file = new File(file, "markdown.md");
        if (file.exists()) {
            try {
                footer = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return footer;
    }

    void onNext(ShareMarkdownAction action) {

        // clean dir first
        try {
            FileUtils.cleanDirectory(targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // prepare pictures
        action.prepare();

        // write file
        File file = action.apply();

        // send notify
        this.onResult(file);

    }

    void prepare() {
        this.pictureMap = new HashMap<>();

        document.getList().stream()
                .filter(e -> e.getClass() == PictureEntity.class)
                .map(e -> (PictureEntity)e)
                .forEach(e -> pictureMap.put(e, getName(e)));
    }

    File apply() {

        File file;

        // markdown file
        {
            file = new File(targetDir, getFileName(entity) + ".md");

            CharSequence markdown = getText(document, this.createFactory(false), this.getFooter());
            markdown = postCreate(markdown);

            String text = markdown.toString();
            file = writeStringToFile(file, text);
        }

        // save picture
        pictureMap.forEach((entity, str) -> {
            try {
                FileUtils.copyFile(entity.getFile(), new File(targetDir, str), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return file;
    }

    CharSequence postCreate(CharSequence text) {
        return text;
    }

    File render() {

        // read template
        String text = AssetUtils.getString(context, "markdown/template.html");
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        // render markdown
        String markdown;
        {
            final Map<Class<? extends DocumentEntity>, Function<? super DocumentEntity, CharSequence>> map = this.createFactory(true);

            // user absolute uri path
            map.put(PictureEntity.class, e -> {
                PictureEntity entity = (PictureEntity)e;
                File file = new File(targetDir, pictureMap.get(entity));

                StringBuilder sb = new StringBuilder();
                sb.append(String.format("![%1$s]", entity.getText()));
                sb.append(String.format("(%1$s)", getPictureUri(file)));

                return sb;
            });


            markdown = getText(document, map, this.getFooter()).toString();
        }

        // convert to html
        text = text.replace("{title}", entity.getName());
        text = text.replace("{markdown}", escape(markdown));

        File file = context.getExternalCacheDir();
        file = new File(file, "preview");
        file.mkdirs();
        file = new File(file, entity.getId() + ".html");
        file = writeStringToFile(file, text);

        return file;
    }

    void accept(File file) {

        // render html
        if (file != null) {
            file = render();
        }

        if (file != null) {
            start(context, UriUtils.fromFile(context, file), "text/html");
        }
    }

    Map<Class<? extends DocumentEntity>, Function<? super DocumentEntity, CharSequence>> createFactory(final boolean preview) {
        HashMap<Class<? extends DocumentEntity>, Function<? super DocumentEntity, CharSequence>> map = new HashMap<>();

        map.put(ParagraphEntity.class, e -> {
            ParagraphEntity entity = (ParagraphEntity)e;
            CharSequence text = entity.getText();
            return text;
        });

        map.put(PictureEntity.class, e -> {
            PictureEntity entity = (PictureEntity)e;
            String name = pictureMap.get(entity);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("![%1$s]", entity.getText()));
            sb.append(String.format("(%1$s)", name));

            return sb;
        });

        map.put(FormulaEntity.class, e -> {
            FormulaEntity entity = (FormulaEntity)e;

            StringBuilder sb = new StringBuilder();

            if (preview) {
                sb.append("<p>");
            }

            String formula = entity.getFormula();
            formula = (TextUtils.isEmpty(formula))? entity.getLatex(): formula;
            if (MathMLTransformer.isMathML(formula)) {
                sb.append(formula);
            } else {
                sb.append("$$\n");
                sb.append(getLatex(formula, preview));
                sb.append("\n$$");
            }

            if (preview) {
                sb.append("</p>");
            }

            return sb;
        });

        return map;
    }

    void onResult(File file) {
        if (file != null && file.exists()) {

            Snackbar snackbar;

            {
                CharSequence text = String.format("已保存到 文档/%1$s。", context.getString(R.string.share_directory));
                snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
            }

            {
                CharSequence text = "查看";
                View.OnClickListener listener = view -> accept(file);

                snackbar.setAction(text, listener);
            }

            snackbar.show();

        } else {

        }
    }

    static String getPictureUri(File file) {
        String path = Uri.fromFile(file).toString();
        return path;
    }

    static String getLatex(String latex, boolean preview) {

        // adaptive for Typora
        {
            while (latex.indexOf("$$") >= 0) {
                latex = latex.replace("$$", "");
            }

            while (latex.indexOf("${") >= 0) {
                latex = latex.replace("${", "");
            }

            while (latex.indexOf("}$") >= 0) {
                latex = latex.replace("}$", "");
            }

        }

        // adaptive for marked
        if (preview) {
            while (latex.indexOf("\n\n") >= 0) {
                latex = latex.replace("\n\n", "\n");
            }

            {
                latex = latex.trim();
            }
        }

        return latex;
    }
}
