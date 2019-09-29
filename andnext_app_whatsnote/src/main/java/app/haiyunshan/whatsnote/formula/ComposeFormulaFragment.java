package app.haiyunshan.whatsnote.formula;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.base.BaseEntry;
import club.andnext.base.BaseTable;
import club.andnext.helper.ClearAssistMenuHelper;
import club.andnext.helper.SoftInputHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.SoftInputUtils;
import club.andnext.xsltml.MathMLTransformer;
import com.google.gson.annotations.SerializedName;
import org.scilab.forge.jlatexmath.*;
import ru.noties.jlatexmath.JLatexMathDrawable;
import ru.noties.jlatexmath.JLatexMathView;

import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFormulaFragment extends Fragment {

    TitleBar titleBar;

    JLatexMathView mathView;

    View resultLayout;
    View resultIcon;
    TextView resultView;

    EditText editText;

    RecyclerView recyclerView;

    String formulaText;

    String latex;
    int formulaWidth;
    int formulaHeight;

    TeXFormula texFormula;

    SoftInputHelper softInputHelper;
    MathMLTransformer mathMLTransformer;

    public ComposeFormulaFragment() {
        int formulaWidth = 0;
        int formulaHeight = 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.softInputHelper = new SoftInputHelper(this.getActivity());
        softInputHelper.addOnSoftInputListener(((helper, visible) -> {
            recyclerView.setVisibility(visible? View.VISIBLE: View.GONE);
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_formula, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            titleBar.setTitle("方程");
            titleBar.setPositiveText("插入");
            titleBar.setPositiveEnable(false);

            titleBar.setNegativeListener(this::onNegativeClick);
            titleBar.setPositiveListener(this::onPositiveClick);
        }

        {
            this.mathView = view.findViewById(R.id.math_view);

            this.resultLayout = view.findViewById(R.id.layout_result);
            this.resultIcon = view.findViewById(R.id.iv_result);
            this.resultView = view.findViewById(R.id.tv_result);
        }

        {
            this.editText = view.findViewById(R.id.edit_text);
            ClearAssistMenuHelper.attach(editText);
            editText.addTextChangedListener(new FormulaWatcher());
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);

            LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layout);

            recyclerView.addItemDecoration(new FormulaDecoration(getActivity()));
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            FormulaProvider provider = new FormulaProvider(getActivity());

            BridgeAdapter adapter = new BridgeAdapter(getActivity(), provider);
            adapter.bind(FormulaEntry.class,
                    new BridgeBuilder(FormulaViewHolder.class, FormulaViewHolder.LAYOUT_RES_ID, new FormulaConsumer()));

            recyclerView.setAdapter(adapter);
        }

        {
            Bundle bundle = this.getArguments();
            String formula = (bundle == null)? "": bundle.getString("formula");
            formula = (formula == null)? "": formula;
            this.formulaText = formula;

            {
                titleBar.setPositiveText(TextUtils.isEmpty(formula) ? "插入" : "更新");
                titleBar.setPositiveEnable(false);
            }

            {
                editText.setText(formula);
                editText.setSelection(formulaText.length());
            }

        }
    }

    void onNegativeClick(View view) {
        SoftInputUtils.hide(getActivity(), editText);

        getActivity().onBackPressed();
    }

    void onPositiveClick(View view) {
        SoftInputUtils.hide(getActivity(), editText);

        Intent intent = new Intent();

        intent.putExtra("formula", editText.getText().toString());

        intent.putExtra("latex", this.latex);
        intent.putExtra("width", this.formulaWidth);
        intent.putExtra("height", this.formulaHeight);

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
    }

    void requestFormula(String text) {

        {
            this.latex = "";
            this.formulaWidth = 0;
            this.formulaHeight = 0;
        }

        if (TextUtils.isEmpty(text)) {
            titleBar.setPositiveEnable(false);

            mathView.setVisibility(View.INVISIBLE);

            resultLayout.setVisibility(View.VISIBLE);
            resultIcon.setVisibility(View.INVISIBLE);
            resultView.setHint("方程预览");

            return;
        }

        String tex = text;
        boolean isMathML = MathMLTransformer.isMathML(tex);
        if (isMathML) {
            if (mathMLTransformer == null) {
                mathMLTransformer = new MathMLTransformer(this.getActivity());
            }

            try {
                tex = mathMLTransformer.transform(tex);
            } catch (TransformerException e) {
                tex = null;
            }

            Log.w("AA", "latext = " + tex);

            if (tex == null) {
                titleBar.setPositiveEnable(false);

                mathView.setVisibility(View.INVISIBLE);

                resultLayout.setVisibility(View.VISIBLE);
                resultIcon.setVisibility(View.VISIBLE);
                resultView.setHint("无效方程");

                return;
            }
        }

        Exception exception = null;
        try {
            mathView.setLatex(tex, true);
        } catch (Exception e) {
            exception = e;
        }

        if (exception != null) {
            titleBar.setPositiveEnable(false);

            mathView.setVisibility(View.INVISIBLE);

            resultLayout.setVisibility(View.VISIBLE);
            resultIcon.setVisibility(View.VISIBLE);
            resultView.setHint("无效方程");

            return;
        }

        boolean isEmpty = true;
        JLatexMathDrawable drawable = mathView.getDrawable();
        if (drawable != null) {
            TeXIcon icon = drawable.getIcon();
            if (icon != null) {
                Box box = icon.getBox();
                if (box != null) {
                    isEmpty = box.isEmpty();
                }
            }
        }

        if (isEmpty) {
            titleBar.setPositiveEnable(false);

            mathView.setVisibility(View.INVISIBLE);

            resultLayout.setVisibility(View.VISIBLE);
            resultIcon.setVisibility(View.VISIBLE);
            resultView.setHint("空方程");

            return;
        }

        {
            titleBar.setPositiveEnable(!formulaText.equals(text));

            mathView.setVisibility(View.VISIBLE);

            resultLayout.setVisibility(View.INVISIBLE);
        }

        {
            this.latex = tex;
            this.formulaWidth = drawable.getIntrinsicWidth();
            this.formulaHeight = drawable.getIntrinsicHeight();
        }

    }

    /**
     *
     */
    private class FormulaWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            requestFormula(text);
        }
    }

    /**
     *
     */
    private class FormulaConsumer implements Consumer<FormulaEntry> {

        @Override
        public void accept(FormulaEntry formulaEntry) {
            Editable text = editText.getText();
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            String target = formulaEntry.text;

            text.replace(start, end, target);
        }
    }

    /**
     *
     */
    private static class FormulaProvider implements BridgeAdapterProvider<FormulaEntry> {

        FormulaTable table;

        public FormulaProvider(Activity context) {
            this.table = GsonUtils.fromJson(context, "formula/formula_ds.json", FormulaTable.class);
            if (table == null) {
                table = new FormulaTable();
            }
        }

        @Override
        public FormulaEntry get(int position) {
            return table.get(position);
        }

        @Override
        public int size() {
            return (table == null)? 0: table.size();
        }
    }

    /**
     *
     */
    private static class FormulaDecoration extends RecyclerView.ItemDecoration {

        int left;
        int right;
        int top;
        int bottom;

        FormulaDecoration(Activity context) {
            Resources res = context.getResources();

            this.left = res.getDimensionPixelSize(R.dimen.formulaLeft);
            this.right = res.getDimensionPixelSize(R.dimen.formulaRight);
            this.top = res.getDimensionPixelSize(R.dimen.formulaTop);
            this.bottom = res.getDimensionPixelSize(R.dimen.formulaBottom);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect,
                                   @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(0, top, right, bottom);
            } else {
                outRect.set(left, top, right, bottom);
            }

        }
    }

    /**
     *
     */
    private static class FormulaViewHolder extends BridgeViewHolder<FormulaEntry> {

        public static final int LAYOUT_RES_ID = R.layout.layout_formula_list_item;

        TextView textView;

        FormulaConsumer consumer;

        @Keep
        public FormulaViewHolder(FormulaConsumer consumer, View itemView) {
            super(itemView);

            this.consumer = consumer;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            this.textView = view.findViewById(R.id.tv_text);

            view.setOnClickListener(this::onItemClick);
        }

        @Override
        public void onBind(FormulaEntry item, int position) {
            textView.setText(item.symbol);
        }

        void onItemClick(View view) {
            FormulaEntry item = this.getItem();
            if (consumer != null && item != null) {
                consumer.accept(item);
            }
        }
    }

    /**
     *
     */
    private static class FormulaTable extends BaseTable<FormulaEntry> {

    }

    /**
     *
     */
    private static class FormulaEntry extends BaseEntry {

        @SerializedName("symbol")
        public String symbol;

        @SerializedName("text")
        public String text;

        public FormulaEntry(String id) {
            super(id);
        }
    }
}
