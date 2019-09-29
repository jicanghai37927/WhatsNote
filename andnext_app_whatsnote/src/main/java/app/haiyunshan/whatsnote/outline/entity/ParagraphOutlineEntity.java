package app.haiyunshan.whatsnote.outline.entity;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.article.entity.ParagraphEntity;

public class ParagraphOutlineEntity extends BaseOutlineEntity<ParagraphEntity> {

    CharSequence content;
    String text;

    public ParagraphOutlineEntity(ParagraphEntity parent, CharSequence content) {
        super(parent);

        this.content = content;
    }

    public CharSequence getContent() {
        return content;
    }

    public String getText() {
        if (text == null) {
            text = content.toString();
        }

        return text;
    }

    @Override
    public void indent(CharSequence prefix) {
        SpannableStringBuilder ssb = this.trim();
        if (!TextUtils.isEmpty(prefix)) {
            ssb.insert(0, prefix);
        }

        this.content = ssb;
        this.text = null;
    }

    SpannableStringBuilder trim() {
        SpannableStringBuilder ssb;
        if (content instanceof SpannableStringBuilder) {
            ssb = (SpannableStringBuilder)content;
        } else {
            ssb = new SpannableStringBuilder(content);
        }

        int pos = indexNoWhitespace(ssb);
        if (pos > 0) {
            ssb.delete(0, pos);
        }

        return ssb;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getText();
    }

    static int indexNoWhitespace(CharSequence text) {
        for (int i = 0, length = text.length(); i < length; i++) {
            char c = text.charAt(i);
            if (!isWhitespace(c)) {
                return i;
            }
        }

        return text.length();
    }

    static boolean isWhitespace(char c) {
        return (c == ' ')
                || (c == '\n')
                || (c == '\r')
                || (c == '\t')
                || (c == '\u3000');
    }
}
