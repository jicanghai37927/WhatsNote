package app.haiyunshan.whatsnote.setting.viewholder;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.MarkedSettingItem;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.core.MarkwonTheme;

public class MarkedSettingViewHolder extends BaseSettingViewHolder<MarkedSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_marked_list_item;

    @Keep
    public MarkedSettingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        {
            view.setOnClickListener(null);
            view.setClickable(false);
        }
    }

    @Override
    public void onBind(MarkedSettingItem item, int position) {
        super.onBind(item, position);

        int type = item.getType();
        String text = item.getText();
        text = ensureText(text);

        switch (type) {
            case MarkedSettingItem.TYPE_PLAIN: {

                textView.setText(text);

                break;
            }

            case MarkedSettingItem.TYPE_MARKDOWN: {

                Context context = getContext();

                Markwon.Builder builder = Markwon.builder(context);
                builder.useTheme(MarkwonTheme.builderWithDefaults(context).linkUnderline(true));
                builder.usePlugin(CorePlugin.create());

                builder.build().setMarkdown(textView, text);

                break;
            }

            case MarkedSettingItem.TYPE_HTML: {

                CharSequence s = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
                textView.setText(s);
                textView.setMovementMethod(LinkMovementMethod.getInstance());

                break;
            }
        }

        {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private static String ensureText(String text) {
        int length = text.length();
        if (length > 0) {
            char last = text.charAt(length - 1);
            if (last != '\n') {
                text += "\n\u3000";
            }
        }

        return text;
    }
}
