package app.haiyunshan.whatsnote.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import app.haiyunshan.whatsnote.R;

public class SearchTitleBar extends LinearLayout {

    View normalBar;
    TitleBar editBar;

    FrameLayout navigationLayout;
    TextView backBtn;

    ViewGroup titleLayout;
    TextView titleView;

    Toolbar toolbar;

    View searchLayout;
    TextView searchBtn;

    public SearchTitleBar(Context context) {
        this(context, null);
    }

    public SearchTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearchTitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        {
            this.setOrientation(VERTICAL);
        }

        {
            int resource = R.layout.merge_search_title_bar;
            LayoutInflater.from(context).inflate(resource, this, true);
        }

        {
            this.normalBar = findViewById(R.id.normal_bar);
            normalBar.setVisibility(View.VISIBLE);

            this.editBar = findViewById(R.id.edit_bar);
            editBar.setVisibility(View.INVISIBLE);

            this.navigationLayout = findViewById(R.id.btn_navigation);
            this.backBtn = findViewById(R.id.btn_back);

            this.titleLayout = findViewById(R.id.title_layout);
            this.titleView = findViewById(R.id.tv_title);

            this.toolbar = findViewById(R.id.toolbar);

            this.searchLayout = findViewById(R.id.search_layout);
            this.searchBtn = findViewById(R.id.tv_search);
        }


        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchTitleBar);

            {
                boolean searchVisible = a.getBoolean(R.styleable.SearchTitleBar_searchVisible, true);
                searchLayout.setVisibility(searchVisible ? View.VISIBLE : View.GONE);
            }

            {
                String title = a.getString(R.styleable.SearchTitleBar_title);
                titleView.setText(title);
                editBar.setTitle(title);
            }

            {
                boolean visible = a.getBoolean(R.styleable.SearchTitleBar_negativeVisible, true);
                editBar.setNegativeVisible(visible);
            }

            a.recycle();
        }

    }

    public void setTitle(CharSequence text) {
        titleView.setText(text);
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public FrameLayout getNavigationLayout() {
        return this.navigationLayout;
    }

    public TextView getBackButton() {
        return this.backBtn;
    }

    public TextView getSearchButton() {
        return this.searchBtn;
    }

    public ViewGroup getTitleLayout() {
        return this.titleLayout;
    }

    public TitleBar getEditBar() {
        return this.editBar;
    }

    public void setSearchVisible(boolean value) {
        searchLayout.setVisibility(value? View.VISIBLE: View.GONE);
    }

    public void setEdit(boolean value) {
        if (!(value ^ isEdit())) {
            return;
        }

        if (value) {
            normalBar.setVisibility(View.INVISIBLE);
            editBar.setVisibility(View.VISIBLE);

        } else {
            normalBar.setVisibility(View.VISIBLE);
            editBar.setVisibility(View.INVISIBLE);
        }

        searchBtn.setEnabled(!value);
    }

    public boolean isEdit() {
        return (editBar.getVisibility() == View.VISIBLE);
    }
}
