package club.andnext.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 *
 */
public class BaseDialogFragment extends AppCompatDialogFragment {

    protected DialogInterface.OnCancelListener onCancelListener;

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme = R.style.ancDialogDim;
        Bundle args = this.getArguments();
        theme = (args != null)? (args.getInt("theme", theme)): theme;

        this.setStyle(STYLE_NO_TITLE, theme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            view.setOnClickListener(this::onContentClick);
        }

    }

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (onCancelListener != null) {
            onCancelListener.onCancel(dialog);
        }
    }

    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        this.onCancelListener = listener;
    }

    public void setTheme(int theme) {
        Bundle args = this.getArguments();
        args = (args == null)? new Bundle(): args;

        args.putInt("theme", theme);

        this.setArguments(args);
    }

    protected void onContentClick(View view) {
        this.cancel();
    }

    protected void cancel() {
        this.dismiss();

        this.onCancel(this.getDialog());
    }
}
