package app.haiyunshan.whatsnote.article.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.article.entity.FormulaEntity;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import club.andnext.helper.SoftInputHelper;
import club.andnext.utils.AlertDialogUtils;
import club.andnext.utils.SoftInputUtils;
import club.andnext.xsltml.MathMLTransformer;
import ru.noties.jlatexmath.JLatexMathView;

import javax.xml.transform.TransformerException;

/**
 *
 */
public class FormulaViewHolder extends ComposeViewHolder<FormulaEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_formula_list_item;

    static final String TAG = FormulaViewHolder.class.getSimpleName();

    View pictureLayout;
    JLatexMathView pictureView;

    EditText editText;

    @Keep
    public FormulaViewHolder(Callback callback, View itemView) {
        super(callback, itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {

        this.pictureLayout = view.findViewById(R.id.picture_layout);
        this.pictureView = view.findViewById(R.id.iv_picture);

        this.editText = view.findViewById(R.id.edit_text);

        {
            this.setTextChangeListener(new TextChangeListener(this, editText));
        }
    }

    @Override
    public void onBind(FormulaEntity item, int position) {
        super.onBind(item, position);

        {
            editText.setEnabled(callback.isEnable());
            editText.setOnFocusChangeListener(this::onEditFocusChanged);
        }

        {
            // set break strategy to request layout
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editText.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
            }

            editText.setText(item.getText());
            editText.setVisibility(editText.length() > 0? View.VISIBLE: View.GONE);
        }

        {
            pictureLayout.setEnabled(callback.isEnable());
            pictureLayout.setOnClickListener(this::onItemClick);
            pictureLayout.setOnLongClickListener(this::onItemLongClick);
        }

        {
            String latex = item.getLatex();
            if (TextUtils.isEmpty(latex)) {

                String formula = item.getFormula();

                if (MathMLTransformer.isMathML(formula)) {
                    MathMLTransformer transformer = WhatsApp.getInstance().getMathMLTransformer();
                    try {
                        formula = transformer.transform(formula);
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }

                if (formula == null) {
                    formula = item.getFormula();
                }

                latex = formula;
            }

            try {
                pictureView.setLatex(latex, true);
            } catch (Exception e) {

            }

        }

    }

    @Override
    public void onSoftInputChanged(SoftInputHelper helper, boolean visible) {
        super.onSoftInputChanged(helper, visible);
        if (!visible && editText.hasFocus()) {
            editText.clearFocus();
        }
    }

    void onItemClick(View view) {
        if (editText.hasFocus()) {
            SoftInputUtils.hide(getContext(), editText);
        }

        this.requestView();
    }

    boolean onItemLongClick(View view) {

        MenuItemClickListener listener = new MenuItemClickListener();

        {
            listener.put(R.id.menu_delete, (id) -> this.requestRemove());
            listener.put(R.id.menu_compose, (id) -> this.requestCompose());
            listener.put(R.id.menu_name, (id) -> this.requestEdit());
        }

        {
            int menuRes = R.menu.menu_compose_picture;
            PopupMenu popupMenu = new PopupMenu(getContext(), itemView);
            popupMenu.inflate(menuRes);
            popupMenu.setOnMenuItemClickListener(listener);
            popupMenu.show();
        }

        return true;
    }

    void onEditFocusChanged(View v, boolean hasFocus) {
        if (!hasFocus) {
            editText.setVisibility(editText.length() > 0? View.VISIBLE: View.GONE);
            SoftInputUtils.hide(getContext(), editText);
        }
    }

    @Override
    void requestRemove() {

        // save document first
        {
            callback.requestSave(this);
        }

        {
            Context context = getContext();
            CharSequence title = "确定删除方程？";
            CharSequence msg = null;
            CharSequence negativeButton = context.getString(android.R.string.cancel);
            CharSequence positiveButton = context.getString(android.R.string.yes);
            DialogInterface.OnClickListener listener = ((dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    super.requestRemove();
                }
            });

            AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
        }
    }

    void requestCompose() {
        callback.compose(this);
    }

    void requestEdit() {
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
        editText.setSelection(editText.length());
        editText.post(()-> SoftInputUtils.show(getContext(), editText));
    }

    @Override
    public void save() {
        entity.setText(editText.getText());
    }

}
