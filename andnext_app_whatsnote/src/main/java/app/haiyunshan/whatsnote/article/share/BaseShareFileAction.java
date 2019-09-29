package app.haiyunshan.whatsnote.article.share;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import app.haiyunshan.whatsnote.article.entity.*;
import club.andnext.helper.PermissionHelper;
import app.haiyunshan.whatsnote.record.entity.ImageEntity;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseShareFileAction extends BaseShareAction {

    protected CharSequence requestMsg;
    protected CharSequence deniedMsg;

    protected File targetDir;

    public BaseShareFileAction(Context context, View view, RecordEntity entity, Document document) {
        super(context, view, entity, document);

        this.requestMsg = "请允许访问设备上的内容，以保存笔记。";
        this.deniedMsg = "请在「权限管理」，设置允许「读写手机存储」，以保存笔记。";
    }

    @Override
    public void execute() {
        PermissionHelper helper = new PermissionHelper((FragmentActivity)context);
        helper.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        helper.setRequestMessage(requestMsg);
        helper.setDeniedMessage(deniedMsg);
        helper.setOnPermissionListener(h -> accept());
        helper.request();
    }

    abstract void accept();

    static String getFileName(RecordEntity entity) {
        String name = entity.getName(); 
        if (TextUtils.isEmpty(name)) {
            return "_";
        }

        String escape = "/\\:*\"<>!?";

        StringBuilder sb = new StringBuilder();

        {
            int i = 0;
            char c = name.charAt(i);
            if (c == '.') {
                c = '_';
            }
            sb.append(c);
        }

        for (int i = 1, length = name.length(); i < length; i++) {
            char c = name.charAt(i);
            if (escape.indexOf(c) >= 0) {
                c = '_';
            }

            sb.append(c);
        }

        return sb.toString();
    }

    static void start(Context context, Uri data, String type) {

        Intent intent = new Intent();
        intent.setFlags(/** Intent.FLAG_ACTIVITY_NO_HISTORY | **/Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(data, type);

        try {
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    static File writeStringToFile(final File target, final String text) {

        File file = target;

        try {

            file.delete();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            if (!text.isEmpty()) {
                fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
            }

            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");

            osw.write(text);

            osw.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();

            file.delete();
            file = null;
        }

        return file;
    }

    static final CharSequence getText(Document document, String footer) {
        StringBuilder sb = new StringBuilder(10 * 1024);

        document.getList().stream()
                .forEach(e -> {
                    Class clz = e.getClass();

                    if (clz == ParagraphEntity.class) {
                        ParagraphEntity entity = (ParagraphEntity)e;

                        sb.append(entity.getText());
                    } else if (clz == PictureEntity.class) {
                        PictureEntity entity = (PictureEntity)e;

                        sb.append(entity.getText());
                    } else if (clz == FormulaEntity.class) {
                        FormulaEntity entity = (FormulaEntity)e;

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
                    }


                    sb.append('\n');
                });

        if (sb.length() > 0) {
            if (!TextUtils.isEmpty(footer)) {
                sb.append('\n');
                sb.append(footer);
            } else {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb;
    }

    static CharSequence getText(Document document,
                                final Map<Class<? extends DocumentEntity>, Function<? super DocumentEntity, CharSequence>> map,
                                String footer) {
        StringBuilder sb = new StringBuilder();

        document.getList().stream()
                .forEach(e -> {
                    Function<? super DocumentEntity, CharSequence> f = map.get(e.getClass());
                    if (f != null) {
                        CharSequence s = f.apply(e);
                        sb.append(s);
                        sb.append('\n');
                    }
                });

        if (sb.length() > 0) {
            if (!TextUtils.isEmpty(footer)) {
                sb.append('\n');
                sb.append(footer);
            } else {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb;
    }

    static String getName(PictureEntity entity) {
        String ext = getExtension(entity);

        ImageEntity e = ImageEntity.obtain(entity.getId(), entity.getDocument().getId());
        CharSequence name = entity.getId();
        if (e.getNumber() > 0) {
            name = e.getName();
        }

        name = String.format("%1$s.%2$s", name, ext);
        return name.toString();
    }

    static String getExtension(PictureEntity entity) {
        String ext = "jpg";

        String mime = getMimeType(entity);
        if (TextUtils.isEmpty(mime)) {
            return ext;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("image/png", "png");
        map.put("image/jpeg", "jpg");
        map.put("image/gif", "gif");
        map.put("image/bmp", "bmp");

        mime = mime.toLowerCase();
        ext = map.getOrDefault(mime, ext);

        return ext;
    }

    static String getMimeType(PictureEntity entity) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(entity.getFile().getAbsolutePath(), options);

        return options.outMimeType;
    }

    static String escape(String s) {

        StringBuilder out = new StringBuilder(s.length() + 128);

        for (int i = 0, length = s.length(); i < length; i++) {
            char c = s.charAt(i);

            /*
             * From RFC 4627, "All Unicode characters may be placed within the
             * quotation marks except for the characters that must be escaped:
             * quotation mark, reverse solidus, and the control characters
             * (U+0000 through U+001F)."
             */
            switch (c) {
                case '"':
                case '\\':
                case '/':
                    out.append('\\').append(c);
                    break;

                case '\t':
                    out.append("\\t");
                    break;

                case '\b':
                    out.append("\\b");
                    break;

                case '\n':
                    out.append("\\n");
                    break;

                case '\r':
                    out.append("\\r");
                    break;

                case '\f':
                    out.append("\\f");
                    break;

                default:
                    if (c <= 0x1F) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                    break;
            }

        }

        return out.toString();
    }

}
