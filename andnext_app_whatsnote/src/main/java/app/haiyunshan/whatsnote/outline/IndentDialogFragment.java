package app.haiyunshan.whatsnote.outline;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.base.OnFragmentInteractionListener;
import app.haiyunshan.whatsnote.option.ListOptionFragment;
import club.andnext.dialog.BaseDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class IndentDialogFragment extends BaseDialogFragment implements OnFragmentInteractionListener {

    public static final String TYPE_CHINESE = IndentFragment.TYPE_CHINESE;
    public static final String TYPE_ENGLISH = IndentFragment.TYPE_ENGLISH;

    static final String INDENT_TYPE = "indentType";

    ViewGroup container;

    BiConsumer<Integer, String> listener;

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_indent, container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.container = view.findViewById(R.id.container);
    }

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            IndentFragment fragment = new IndentFragment();
            FragmentTransaction t = getChildFragmentManager().beginTransaction();
            t.replace(container.getId(), fragment, "indent");
            t.commit();
        }
    }

    @Override
    public void onInteraction(Fragment from, Uri uri) {
        String scheme = uri.getScheme();
        switch (scheme) {
            case "indent": {
                String action = uri.getQueryParameter("action");
                if (action.equals("cancel")) {
                    this.cancel();
                } else if (action.equals("done")) {
                    if (listener != null) {
                        int count = Integer.parseInt(uri.getQueryParameter("count"));
                        String type = uri.getQueryParameter("type");

                        listener.accept(count, type);

                        this.dismiss();
                    } else {
                        this.cancel();
                    }

                } else if (action.equals("type")) {
                    String type = uri.getQueryParameter("type");

                    requestType(type);
                }

                break;
            }
            case "option": {
                String id = uri.getQueryParameter("id");

                if (id.equals(INDENT_TYPE)) {
                    int index = Integer.parseInt(uri.getQueryParameter("index"));
                    this.onResultType(index);
                }
            }
        }
    }

    public void setOnIndentListener(BiConsumer<Integer, String> listener) {
        this.listener = listener;
    }

    void requestType(String type) {

        {
            ListOptionFragment fragment = new ListOptionFragment();

            {
                String title = "缩进字符类型";
                ArrayList<CharSequence> list = new ArrayList<>(Arrays.asList("中文", "英文"));
                int selectedIndex = (type.equalsIgnoreCase(IndentFragment.TYPE_CHINESE)) ? 0 : 1;
                fragment.setArguments(INDENT_TYPE, title, list, selectedIndex);
            }

            FragmentTransaction t = getChildFragmentManager().beginTransaction();
            t.addToBackStack("type");
            t.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            t.replace(container.getId(), fragment, "type");


            t.commit();
        }
    }

    void onResultType(int index) {
        FragmentManager fm = getChildFragmentManager();
        Fragment f = fm.findFragmentByTag("indent");
        if (f != null && f instanceof IndentFragment) {
            IndentFragment fragment = (IndentFragment)f;

            String type = (index == 0) ? IndentFragment.TYPE_CHINESE : IndentFragment.TYPE_ENGLISH;
            fragment.setArguments(type);
        }

        fm.popBackStack();
    }
}
