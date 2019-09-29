package app.haiyunshan.whatsnote.article.share;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.article.entity.FormulaEntity;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.directory.Directory;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import club.andnext.utils.FileUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ShareClipboardAction extends BaseShareAction {

    public ShareClipboardAction(Context context, View view, RecordEntity entity, Document document) {
        super(context, view, entity, document);
    }

    @Override
    public void execute() {

        {
            List<ClipData.Item> list = document.getList().stream()
                    .map(e -> {
                        ClipData.Item item;

                        Class clz = e.getClass();
                        if (clz == ParagraphEntity.class) {
                            ParagraphEntity entity = (ParagraphEntity)e;

                            item = new ClipData.Item(entity.getText());
                        } else if (clz == PictureEntity.class) {
                            PictureEntity entity = (PictureEntity)e;

                            item = new ClipData.Item(entity.getText());
                        } else if (clz == FormulaEntity.class) {
                            FormulaEntity entity = (FormulaEntity)e;

                            StringBuilder sb = new StringBuilder();

                            // formula
                            if (!TextUtils.isEmpty(entity.getFormula())) {
                                sb.append(entity.getFormula());
                            } else {
                                sb.append(entity.getLatex());
                            }

                            // description
                            if (!TextUtils.isEmpty(entity.getText())) {
                                sb.append('\n');
                                sb.append(entity.getText());
                            }

                            item = new ClipData.Item(sb);
                        } else {
                            item = new ClipData.Item("");
                        }

                        return item;
                    })
                    .collect(Collectors.toList());

            ClipDescription description = new ClipDescription(entity.getName(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN});
            ClipData data = new ClipData(description, list.get(0));

            for (int i = 1; i < list.size(); i++) {
                data.addItem(list.get(i));
            }

            if (data.getItemCount() > 0) {
                String footer = getFooter();
                if (!TextUtils.isEmpty(footer)) {
                    data.addItem(new ClipData.Item(""));
                    data.addItem(new ClipData.Item(""));
                    data.addItem(new ClipData.Item(footer));
                }
            }

            ClipboardManager clipboardManager = (ClipboardManager) (context.getSystemService(Context.CLIPBOARD_SERVICE));
            clipboardManager.setPrimaryClip(data);
        }

        {
            CharSequence text = "内容已复制到粘贴板。";
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    public String getFooter() {
        String footer = "";

        File file = Directory.get(context, Directory.DIR_FOOTER);
        file = new File(file, "text.txt");
        if (file.exists()) {
            try {
                footer = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return footer;
    }

}
