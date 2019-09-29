package app.haiyunshan.whatsdownloader;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.nodes.Element;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import club.andnext.utils.FileUtils;
import club.andnext.utils.WebsiteUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadDialogFragment extends DialogFragment {

    private static String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1";

    ImageView pictureView;
    TextView progressView;
    Button actionBtn;

    String title;
    String url;
    List<Element> list;

    File targetDir;
    HashMap<Element, File> fileMap;

    boolean disposed = false;
    Disposable disposable;

    public DownloadDialogFragment(String title, String url, List<Element> list) {
        this.title = title;
        this.url = url;
        this.list = list;

        this.fileMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_download, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.pictureView = view.findViewById(R.id.iv_picture);
        this.progressView = view.findViewById(R.id.tv_progress);
        this.actionBtn = view.findViewById(R.id.btn_action);
        actionBtn.setOnClickListener(v -> this.cancel());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        this.setCancelable(false);

        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.download();
    }

    void download() {

        {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d/%d", fileMap.size(), list.size()));
            progressView.setText(sb);
        }

        OkHttpClient httpClient = DownloadManager.getInstance().getHttpClient();

        ObservableOnSubscribe<Element> task = emitter -> {

            for (Element e : list) {
                if (disposed) {
                    break;
                }

                String url = getUrl(e);
                Request request = new Request.Builder()
                        .url(WebsiteUtils.format(url))
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", USER_AGENT)
                        .build();

                Call call = httpClient.newCall(request);

                Response response = call.execute();
                InputStream is = response.body().byteStream();
                FileUtils.copyInputStreamToFile(is, getFile(e));
                response.close();

                emitter.onNext(e);
            }

            emitter.onComplete();

        };

        io.reactivex.functions.Consumer<Element> onNext = (e) -> {
            File file = getFile(e);
            fileMap.put(e, file);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d/%d", fileMap.size(), list.size()));

            progressView.setText(sb);

            Glide.with(pictureView)
                    .load(file)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(pictureView);
        };

        io.reactivex.functions.Consumer<Throwable> onError = (e) -> {
            e.printStackTrace();

            StringBuilder sb = new StringBuilder();
            sb.append("下载出错！");
            sb.append("\n");
            sb.append(String.format("%d/%d", fileMap.size(), list.size()));
            sb.append("\n");
            sb.append(e.toString());

            progressView.setText(sb);
        };

        Action onComplete = () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("下载完成！");
            sb.append("\n");
            sb.append(getDir().toString());

            progressView.setText(sb);
            actionBtn.setText("完成");
        };

        this.disposable = Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete);
    }

    String getUrl(Element element) {
        String src = element.attr("src");

        URL url = null;
        try {
            URL parent = new URL(this.url);
            url = new URL(parent, src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url != null) {
            return url.toString();
        }

        return src;
    }

    File getFile(Element element) {
        int pos = list.indexOf(element);
        pos += 1;

        String name = String.format("IMG_%04d.jpg", pos);
        return new File(getDir(), name);
    }

    File getDir() {
        if (targetDir != null) {
            return targetDir;
        }

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File dir = root;
        dir = new File(dir, title);
        dir.mkdirs();

        if (dir.exists()) {
            targetDir = dir;

            return dir;
        }

        dir = root;
        dir = new File(dir, "" + System.currentTimeMillis());
        dir.mkdirs();

        targetDir = dir;
        return targetDir;
    }

    void cancel() {
        this.disposed = true;

        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }

        this.dismiss();
    }
}
