package app.haiyunshan.whatsandroid;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

import app.haiyunshan.whatsandroid.remote.TestRemoteManager;
import club.andnext.utils.WebsiteUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestUrlActivity extends AppCompatActivity {

    TextView resultView;
    FrameLayout containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_url);

        this.resultView = findViewById(R.id.tv_result);
        this.containerLayout = findViewById(R.id.container);

        findViewById(R.id.btn_linkify).setOnClickListener(this::onLikifyClick);
        findViewById(R.id.btn_validate).setOnClickListener(this::onValidateClick);
        findViewById(R.id.btn_info).setOnClickListener(this::onInfoClick);
        findViewById(R.id.btn_mime).setOnClickListener(this::onMimeClick);
        findViewById(R.id.btn_count).setOnClickListener(this::onCountClick);

        EditText editText = findViewById(R.id.edit_text);

//        new WebsiteHelper(this, "http://www.baidu.com", null).get();

        // true, false, false
        editText.setText("http://.com");

        // true, true, false
        editText.setText("https://baike.baidu.com/item/%E8%8B%92/5359381?fr=aladdin");

        // false, true, false
        editText.setText("baike.baidu.com/item/%E8%8B%92/5359381?fr=aladdin");

        // true, true, false
        editText.setText("https://mp.weixin.qq.com/s/-dnZcnxgEF27q6ZCwF55VQ");

        // true, true, true
        editText.setText("https://so.gushiwen.org/shiwenv_b087946a7ba7.aspx");

        // false, true, true
        editText.setText("so.gushiwen.org/shiwenv_b087946a7ba7.aspx");

        // true, true, true
        editText.setText("so.gushiwen.org/shiwenv_b087946a7ba7.aspx");

        // true, true, false
        editText.setText("https://www.baidu.com/s?ie=UTF-8&wd=%E4%BA%BA%E7%94%9F%E5%BE%97%E6%84%8F%E9%A1%BB%E5%B0%BD%E6%AC%A2");

        // true, true, false
        editText.setText("https://m.baidu.com/?from=844b&vit=fps#iact=wiseindex%2Ftabs%2Fnews%2Factivity%2Fnewsdetail%3D%257B%2522linkData%2522%253A%257B%2522name%2522%253A%2522iframe%252Fmib-iframe%2522%252C%2522id%2522%253A%2522feed%2522%252C%2522index%2522%253A0%252C%2522url%2522%253A%2522https%253A%252F%252Fmbd.baidu.com%252Fnewspage%252Fdata%252Flandingpage%253Fs_type%253Dnews%2526dsp%253Dwise%2526context%253D%25257B%252522nid%252522%25253A%252522news_9889638205896278325%252522%25257D%2526pageType%253D1%2526n_type%253D1%2526p_from%253D-1%2526innerIframe%253D1%2522%252C%2522title%2522%253Anull%257D%257D");

        editText.setText("https://m.baidu.com/?from=844b&vit=fps#iact=wiseindex%2Ftabs%2Fnews%2Factivity%2Fnewsdetail%3D%257B%2522linkData%2522%253A%257B%2522name%2522%253A%2522iframe%252Fmib-iframe%2522%252C%2522id%2522%253A%2522feed%2522%252C%2522index%2522%253A0%252C%2522url%2522%253A%2522https%253A%252F%252Fmbd.baidu.com%252Fnewspage%252Fdata%252Flandingpage%253Fs_type%253Dnews%2526dsp%253Dwise%2526context%253D%25257B%252522nid%252522%25253A%252522news_9725999878890118536%252522%25257D%2526pageType%253D1%2526n_type%253D1%2526p_from%253D-1%2526innerIframe%253D1%2522%252C%2522isThird%2522%253Afalse%252C%2522title%2522%253Anull%257D%257D");

        editText.setText("so.gushiwen.org");

//        editText.setText("https://www.jianshu.com/p/95d4d73be3d1");

        editText.setText("http://v.douyin.com/yVKsUr");

        editText.setText("https://www.iesdouyin.com/share/video/6697583385646353671/?region=CN&mid=6688524072705542920&titleType=title&timestamp=1566454483&utm_campaign=client_share&app=aweme&utm_medium=ios&tt_from=copy&utm_source=copy");

        editText.setText("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566558670967&di=131d93b5781fedde3a9891c77fe62f0e&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130314%2F11899688_192542628000_2.jpg");

//        editText.setText("http://o-oo.net.cn/50yintuceshi/a.mp3");

//        editText.setText("https://music.163.com/m/song?id=5199181&userid=74835004");
//
//        editText.setText("andnext.club");

        editText.setText("https://page.om.qq.com/page/OuFafqB-rj9KwUrQ_Z363Ivg0?source=omapp");

        editText.setText("");
    }

    @Override
    public void onBackPressed() {
        if (containerLayout.getChildCount() != 0) {
            containerLayout.removeAllViews();
            return;
        }

        super.onBackPressed();
    }

    void onLikifyClick(View view) {
        EditText editText = findViewById(R.id.edit_text);
        String text = editText.getText().toString();

        resultView.setText(text);
    }

    void onValidateClick(View view) {

        EditText editText = findViewById(R.id.edit_text);
        String text = editText.getText().toString();

        StringBuilder sb = new StringBuilder();

        List<String> list = WebsiteUtils.toSegments(text);
        sb.append("count = " + list.size());
        sb.append("\n\n");

        for (String str: list) {
//            str = str.trim();
            boolean isUrl = WebsiteUtils.isURL(str);
            if (isUrl) {
                str = WebsiteUtils.decode(str);
            }

            sb.append(str);
            sb.append('\n');
            sb.append("isUrl = " + isUrl);
            if (isUrl) {
                sb.append('\n');
                String domain = WebsiteUtils.getDomainName(str);
                sb.append("domain = " + domain);
            }

            sb.append("\n\n");
        }

        resultView.setText(sb);
    }

    void onInfoClick(View view) {

    }

    void onMimeClick(View view) {
        EditText editText = findViewById(R.id.edit_text);
        String text = editText.getText().toString().trim();

        OkHttpClient client = TestRemoteManager.getInstance().getHttpClient();
        Request request = new Request.Builder()
                .url(text)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                resultView.post(() -> {
                    resultView.setText(e.toString());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();

                MediaType type = response.body().contentType();
                type.type();

                resultView.post(() -> {
                    resultView.setText("mime type = " + type.type() + "/" + type.subtype());
                    resultView.append("\n" + "code = " + code);
                });
            }
        });
    }

    void onCountClick(View view) {
        EditText editText = findViewById(R.id.edit_text);
        String text = editText.getText().toString();

        boolean a = WebsiteUtils.isURL(text.trim());
        boolean b = Patterns.DOMAIN_NAME.matcher(text.trim()).matches();

        resultView.setText("count = " + WebsiteUtils.countOf(text));
        resultView.append("\n" + "web url = " + a);
        resultView.append("\n" + "domain name = " + b);
    }

}
