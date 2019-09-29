package app.haiyunshan.whatsnote.setting;


import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.MarkedSettingItem;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.core.MarkwonTheme;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleTextFragment extends Fragment {

    public static final int TYPE_PLAIN      = MarkedSettingItem.TYPE_PLAIN;
    public static final int TYPE_MARKDOWN   = MarkedSettingItem.TYPE_MARKDOWN;
    public static final int TYPE_HTML       = MarkedSettingItem.TYPE_HTML;

    SearchTitleBar titleBar;

    TextView textView;

    public SimpleTextFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);

            Toolbar toolbar = titleBar.getToolbar();
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(this::onNavigationClick);
        }

        {
            this.textView = view.findViewById(R.id.tv_text);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        titleBar.setTitle(args.getString("title"));

        int type = args.getInt("type", TYPE_PLAIN);
        String text = args.getString("text", "");
        text = ensureText(text);

        switch (type) {
            case TYPE_PLAIN: {

                textView.setText(text);

                break;
            }

            case TYPE_MARKDOWN: {

                Context context = getActivity();

                Markwon.Builder builder = Markwon.builder(context);
                builder.useTheme(MarkwonTheme.builderWithDefaults(context).linkUnderline(true));
                builder.usePlugin(CorePlugin.create());

                builder.build().setMarkdown(textView, text);

                break;
            }

            case TYPE_HTML: {

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


    void onNavigationClick(View view) {
        getActivity().onBackPressed();
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
