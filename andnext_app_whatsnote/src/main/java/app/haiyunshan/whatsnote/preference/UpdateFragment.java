package app.haiyunshan.whatsnote.preference;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.AutoDownloadActivity;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.SimpleTextActivity;
import app.haiyunshan.whatsnote.base.RequestResultDelegate;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.preference.entity.PreferenceEntity;
import app.haiyunshan.whatsnote.remote.RemoteManager;
import app.haiyunshan.whatsnote.remote.RemoteService;
import app.haiyunshan.whatsnote.rxjava2.RxDisposer;
import app.haiyunshan.whatsnote.setting.BasePreferenceFragment;
import app.haiyunshan.whatsnote.setting.item.*;
import app.haiyunshan.whatsnote.setting.viewholder.*;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import app.haiyunshan.whatsnote.update.VersionEntry;
import club.andnext.download.DownloadEntity;
import club.andnext.download.DownloadListener;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.utils.AlertDialogUtils;
import club.andnext.utils.MD5Utils;
import club.andnext.utils.PackageUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends BasePreferenceFragment {

    private static final String ID_VERSION          = "VERSION";
    private static final String ID_DOWNLOAD         = "DOWNLOAD";
    private static final String ID_AUTO_DOWNLOAD    = "AUTO_DOWNLOAD";
    private static final String ID_DOWNLOAD_TIP     = "DOWNLOAD_TIP";

    protected View loadingView;

    RxDisposer disposer;
    RequestResultManager requestResultManager;
    DownloadListener downloadListener;

    public UpdateFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestResultManager = new RequestResultManager();
        this.downloadListener = new VersionDownloadListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.setTitle("软件更新");
        }

        {
            this.loadingView = view.findViewById(R.id.layout_loading);
        }

        {
            RecyclerView.ItemAnimator animator = new FadeInDownAnimator();
            recyclerView.setItemAnimator(animator);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.disposer = new RxDisposer(getActivity());
        }

        DownloadEntity downloadEntity = UpdateEntity.obtain().getDownload();
        if (downloadEntity != null && downloadEntity.getStatus() == DownloadListener.STATUS_RUNNING) {
            loadingView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            UpdateEntity entity = UpdateEntity.obtain();

            List<BaseSettingItem> list = entity.exist()? createVersionList(): createLatestList();
            this.replaceAll(list);
        } else {
            this.requestVersion();
        }

        if (downloadEntity != null) {
            downloadEntity.registerObserver(downloadListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DownloadEntity entity = UpdateEntity.obtain().getDownload();
        if (entity != null) {
            entity.unregisterAll();
        }

    }

    void requestVersion() {

        io.reactivex.functions.Consumer<? super VersionEntry> onNext = (version) -> {

            // update version info
            UpdateEntity entity = UpdateEntity.obtain();
            entity.setVersion(version);
            entity.save();
        };

        io.reactivex.functions.Consumer<? super Throwable> onError = (throwable) -> {

            if (throwable != null) {
                Log.w("Update", throwable.toString());
            }

            {
                Context context = getContext();
                CharSequence title = "无法检查更新";
                CharSequence msg = "检查软件更新时出错。";
                CharSequence negativeButton = "取消";
                CharSequence positiveButton = "再试一次";
                DialogInterface.OnClickListener listener = ((dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        requestVersion();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        getActivity().onBackPressed();
                    }
                });

                Dialog dialog = AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnCancelListener((d) -> getActivity().onBackPressed());
            }
        };

        Action onComplete = () -> {
            loadingView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            UpdateEntity entity = UpdateEntity.obtain();

            List<BaseSettingItem> list = entity.exist()? createVersionList(): createLatestList();
            this.replaceAll(list);
        };

        {
            RemoteService service = RemoteManager.getInstance().getService();
            Observable<VersionEntry> o = service.getVersion();
            Disposable d = o.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onNext, onError, onComplete);
            disposer.add(d);
        }

    }

    void requestDownload() {
        final UpdateEntity updateEntity = UpdateEntity.obtain();

        DownloadEntity downloadEntity = updateEntity.getDownload();
        if (downloadEntity == null) {
            downloadEntity = updateEntity.download();
        } else {
            int status = downloadEntity.getStatus();
            if (status == DownloadListener.STATUS_SUCCESSFUL) {
                downloadEntity = null;
            } else if (status == DownloadListener.STATUS_RUNNING) {
                downloadEntity = null;
            } else {
                updateEntity.download();
            }
        }

        if (downloadEntity != null) {
            downloadEntity.registerObserver(downloadListener);

            notifyItemChanged(getItem(ID_VERSION));
            notifyItemChanged(getItem(ID_DOWNLOAD));
        }
    }

    void requestVerify() {

        ObservableOnSubscribe<String> task = emitter -> {
            File file = UpdateEntity.obtain().getDestination();
            String digest = MD5Utils.getFileMD5(file).toLowerCase();
            emitter.onNext(digest);

            emitter.onComplete();
        };


        io.reactivex.functions.Consumer<String> onNext = (digest) -> {

            String checksum = UpdateEntity.obtain().getChecksum().toLowerCase();
            if (checksum.equals(digest)) {
                requestInstall();
            } else {

                Context context = getContext();
                CharSequence title = "验证更新失败";
                CharSequence msg = "更新文件已经损坏。";
                CharSequence negativeButton = "关闭";
                CharSequence positiveButton = "重新下载";
                DialogInterface.OnClickListener listener = ((dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        UpdateEntity.obtain().deleteDownload();
                        requestDownload();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                    }
                });

                Dialog dialog = AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
                dialog.setCanceledOnTouchOutside(false);
            }
        };

        Disposable d = Observable.create(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext);

        disposer.add(d);
    }

    void requestInstall() {
        PackageUtils.install(getActivity(), UpdateEntity.obtain().getDestination());
    }

    @Override
    protected List<? extends BaseSettingItem> createList() {
        return null;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {

        adapter.bind(CaptionSettingItem.class,
                new BridgeBuilder(CaptionSettingViewHolder.class, CaptionSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(VersionSettingItem.class,
                new BridgeBuilder(VersionSettingViewHolder.class, VersionSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(MarkedSettingItem.class,
                new BridgeBuilder(MarkedSettingViewHolder.class, MarkedSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(UpdateSettingItem.class,
                new BridgeBuilder(UpdateSettingViewHolder.class, UpdateSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(ExplainSettingItem.class,
                new BridgeBuilder(ExplainSettingViewHolder.class, ExplainSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(BaseSettingItem.class,
                new BridgeBuilder(BaseSettingViewHolder.class, BaseSettingViewHolder.LAYOUT_RES_ID));
    }

    List<BaseSettingItem> createVersionList() {
        ArrayList<BaseSettingItem> list = new ArrayList<>();

        {
            VersionSettingItem item = new VersionSettingItem(UpdateEntity.obtain());
            item.setId(ID_VERSION);
            item.setDividerVisible(false);

            list.add(item);
        }

        {
            MarkedSettingItem item = new MarkedSettingItem(UpdateEntity.obtain().getBrief());

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setName("了解更多");
            item.setChevron(View.VISIBLE);
            item.setConsumer((object) -> {
                String text = UpdateEntity.obtain().getDetail();
                SimpleTextActivity.start(this, "关于本更新", text, SimpleTextActivity.TYPE_MD);
            });

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        {
            final UpdateEntity updateEntity = UpdateEntity.obtain();

            UpdateSettingItem item = new UpdateSettingItem(updateEntity);
            item.setId(ID_DOWNLOAD);
            item.setChevron(View.GONE);
            item.setConsumer((object -> {
                if (updateEntity.getDestination().exists()) {
                    requestVerify();
                } else {
                    requestDownload();
                }
            }));

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        {
            this.appendAutoDownload(list);
        }


        return list;
    }

    List<BaseSettingItem> createLatestList() {
        ArrayList<BaseSettingItem> list = new ArrayList<>();

        {
            this.appendAutoDownload(list);
        }

        {
            CaptionSettingItem item = new CaptionSettingItem();
            String text = getString(R.string.app_name);
            String versionName = PackageUtils.getVersionName(getActivity());
            if (!TextUtils.isEmpty(versionName)) {
                text = text + " " + versionName;
            }
            text += "\n您的软件是最新版本。";


            item.setHint(text);

            list.add(item);

        }

        return list;
    }

    void appendAutoDownload(List<BaseSettingItem> list) {

        {
            BaseSettingItem item = new BaseSettingItem();
            item.setId(ID_AUTO_DOWNLOAD);
            item.setName("自动下载");
            item.setHintSupplier(() ->  {
                PreferenceEntity entity = PreferenceEntity.obtain();
                String text = entity.isAutoDownload()? "打开": "关闭";
                return text;
            });

            item.setConsumer((obj) -> {
                RequestResultDelegate delegate = new RequestResultDelegate(this,
                        ((f, c) -> {
                            AutoDownloadActivity.startForResult(f, c);
                            return true;
                        }),
                        (r, i) -> {
                            notifyItemChanged(getItem(ID_AUTO_DOWNLOAD));
                            notifyItemChanged(getItem(ID_DOWNLOAD_TIP));
                        });

                requestResultManager.request(delegate);
            });

            item.setChevron(View.VISIBLE);

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setId(ID_DOWNLOAD_TIP);

            item.setHintSupplier(() -> {
                PreferenceEntity entity = PreferenceEntity.obtain();

                String text = "发现新版本时，自动下载安装包。";
                text = (entity.isAutoDownload())? text: "";
                return text;
            });

            list.add(item);
        }

    }

    /**
     *
     */
    private class VersionDownloadListener implements DownloadListener {

        @Override
        public void onStatusChanged(DownloadEntity entity, int status) {
            if (status == STATUS_SUCCESSFUL) {
                this.showSuccessful();
            } else if (status == STATUS_FAILED) {
                this.showFailed();
            }
        }

        @Override
        public void onProgressUpdate(DownloadEntity entity, long progress, long max, long speed) {

        }

        void showFailed() {
            UpdateEntity updateEntity = UpdateEntity.obtain();

            Context context = getContext();
            CharSequence title = "软件更新失败";
            CharSequence msg = "下载“" + updateEntity.getTitle() + " " + updateEntity.getName() + "”时出错。";
            CharSequence negativeButton = "关闭";
            CharSequence positiveButton = "再试一次";
            DialogInterface.OnClickListener listener = ((dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    requestDownload();
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                }
            });

            Dialog dialog = AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
            dialog.setCanceledOnTouchOutside(false);
        }

        void showSuccessful() {
            UpdateEntity updateEntity = UpdateEntity.obtain();

            Context context = getContext();
            CharSequence title = "软件更新";
            CharSequence msg = "“" + updateEntity.getTitle() + " " + updateEntity.getName() + "”下载完成。";
            CharSequence negativeButton = "稍后";
            CharSequence positiveButton = "现在安装";
            DialogInterface.OnClickListener listener = ((dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    requestVerify();
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                }
            });

            Dialog dialog = AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
            dialog.setCanceledOnTouchOutside(false);
        }
    }
}
