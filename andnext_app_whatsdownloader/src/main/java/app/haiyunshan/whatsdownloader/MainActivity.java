package app.haiyunshan.whatsdownloader;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import club.andnext.helper.PermissionHelper;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.utils.WebsiteUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Downloader";
    private static String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1";

    EditText urlEdit;
    WebView webView;

    RecyclerView recyclerView;

    View listLayout;
    TextView countView;
    View downloadBtn;

    BridgeAdapter adapter;
    List<Element> list;

    List<Element> checkedList;

    String title;
    String htmlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.urlEdit = findViewById(R.id.edit_url);
        configEditText(urlEdit);

        this.webView = findViewById(R.id.webview);
        configWebView(webView);

        this.recyclerView = findViewById(R.id.recycler_list_view);
        configRecyclerView(recyclerView);

        this.listLayout = findViewById(R.id.layout_list);
        this.countView = findViewById(R.id.tv_count);
        this.downloadBtn = findViewById(R.id.btn_download);

        listLayout.setVisibility(View.INVISIBLE);

        findViewById(R.id.btn_refresh).setOnClickListener(view -> {
            String url = urlEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(url)) {
                loadUrl(url);
            }

//            listLayout.setVisibility(View.INVISIBLE);
//            webView.reload();
        });

        downloadBtn.setOnClickListener(view -> download());
    }

    void download() {

        String requestMsg = "请允许访问设备上的内容，以保存图片。";
        String deniedMsg = "请在「权限管理」，设置允许「读写手机存储」，以保存图片。";

        PermissionHelper.OnPermissionListener listener = (helper) -> {
            String title = MainActivity.this.title;
            List<Element> list = getDownloadList();

            DownloadDialogFragment dialogFragment = new DownloadDialogFragment(title, webView.getUrl(), list);
            dialogFragment.show(getSupportFragmentManager(),"dialog");
        };

        PermissionHelper helper = new PermissionHelper(this);
        helper.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        helper.setRequestMessage(requestMsg);
        helper.setDeniedMessage(deniedMsg);
        helper.setOnPermissionListener(listener);
        helper.request();
    }

    void loadUrl(String url) {
        listLayout.setVisibility(View.INVISIBLE);

        webView.loadUrl(url);
    }

    void configRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
    }

    void configEditText(EditText editText) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!TextUtils.isEmpty(url)) {
                    loadUrl(url);
                }
            }
        };

        editText.addTextChangedListener(watcher);
    }

    void configWebView(WebView webView) {

        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

        WebSettings settings = webView.getSettings();
        settings.setUserAgentString(USER_AGENT);

        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkLoads(false);

        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        settings.setBlockNetworkImage(true);
        settings.setLoadsImagesAutomatically(false);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        settings.setMediaPlaybackRequiresUserGesture(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    void onHtmlReady() {
        String html = htmlContent;
        if (TextUtils.isEmpty(html)) {
            return;
        }

        listLayout.setVisibility(View.VISIBLE);

        this.list = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements jpgs = doc.select("img[src]");

        for (int i = 0; i < jpgs.size(); i++) {
            Element e = jpgs.get(i);
            String parent = e.parent().tagName();
            if (parent.equalsIgnoreCase("a")) {
                continue;
            }

            list.add(e);
        }

        this.checkedList = new ArrayList<>(list);

        ClazzAdapterProvider<Element> provider = new ClazzAdapterProvider<Element>() {
            @Override
            public Element get(int position) {
                return list.get(position);
            }

            @Override
            public int size() {
                return list.size();
            }
        };

        this.adapter = new BridgeAdapter(this, provider);
        adapter.bind(Element.class, new BridgeBuilder(UrlViewHolder.class, UrlViewHolder.LAYOUT_RES_ID, this));

        recyclerView.setAdapter(adapter);

        onCheckedChanged();
    }

    void onCheckedChanged() {
        String text = String.format("%d/%d", checkedList.size(), list.size());
        countView.setText(text);

        downloadBtn.setEnabled(!checkedList.isEmpty());
    }

    List<Element> getDownloadList() {
        ArrayList<Element> out = new ArrayList<>();

        for (Element e : list) {
            if (checkedList.contains(e)) {
                out.add(e);
            }
        }

        return out;
    }

    /**
     *
     */
    private class UrlViewHolder extends BridgeViewHolder<Element> {

        public static final int LAYOUT_RES_ID = R.layout.layout_img_list_item;

        CheckBox checkBox;
        TextView nameView;

        @Keep
        public UrlViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.checkBox = view.findViewById(R.id.cb_box);
            this.nameView = view.findViewById(R.id.tv_name);
        }

        @Override
        public void onBind(Element item, int position) {
            checkBox.setChecked(checkedList.contains(item));
            nameView.setText(item.attr("src"));
        }

        void onItemClick(View view) {
            Element item = getItem();
            if (item == null) {
                return;
            }

            if (checkedList.contains(item)) {
                checkedList.remove(item);
            } else {
                checkedList.add(item);
            }

            checkBox.setChecked(checkedList.contains(item));

            onCheckedChanged();
        }
    }
    /**
     *
     */
    @Keep
    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            Log.w(TAG, "onReceivedTitle = " + title);

            MainActivity.this.title = title;
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            Log.w(TAG, "progress = " + newProgress);
        }
    }

    /**
     *
     */
    @Keep
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Log.w(TAG, "shouldOverrideUrlLoading = " + request.getUrl());

            Uri uri = request.getUrl();
            if (uri != null) {
                String scheme = uri.getScheme();
                if (!(scheme.equalsIgnoreCase("http")
                        || scheme.equalsIgnoreCase("https"))) {
                    return true;
                }

                String url = uri.toString().toLowerCase();
                if (url.indexOf("apple-store/") >= 0) {
                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            Log.v(TAG, "onReceivedError = " + error.getErrorCode() + ", " + request.getUrl());

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);

            Log.w(TAG, "onReceivedHttpError = " + errorResponse.getStatusCode() + ", " + errorResponse.getMimeType());

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            Log.w(TAG, "onPageStarted");

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Log.v(TAG, "onPageFinished");

            webView.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
        }

    }

    /**
     *
     */
    @Keep
    final class InJavaScriptLocalObj {

        public InJavaScriptLocalObj() {

        }

        @Keep
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showSource(String html) {
            Log.w(TAG, "html ready.");

            htmlContent = html;
            webView.getHandler().post(MainActivity.this::onHtmlReady);
        }
    }

}
