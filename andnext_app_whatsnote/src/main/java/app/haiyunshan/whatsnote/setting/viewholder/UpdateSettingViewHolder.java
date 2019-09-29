package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.UpdateSettingItem;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import club.andnext.download.DownloadEntity;
import club.andnext.download.DownloadListener;

public class UpdateSettingViewHolder extends BaseSettingViewHolder<UpdateSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_update_list_item;

    static final int STATE_NONE         = 1;
    static final int STATE_RUNNING      = 2;
    static final int STATE_SUCESSFUL    = 3;

    ProgressBar progressBar;

    UpdateEntity updateEntity;
    DownloadListener downloadListener;

    @Keep
    public UpdateSettingViewHolder(View itemView) {
        super(itemView);

        this.updateEntity = UpdateEntity.obtain();
        this.downloadListener = new MyDownloadListener();
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        this.nameView.setTextColor(view.getContext().getColorStateList(R.color.selector_action_menu));

        this.progressBar = view.findViewById(R.id.progress_circular);
    }

    @Override
    public void onBind(UpdateSettingItem item, int position) {
        super.onBind(item, position);

        {
            this.updateItem();
        }

        DownloadEntity entity = updateEntity.getDownload();
        if (entity != null) {
            entity.registerObserver(downloadListener);
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();

        DownloadEntity entity = updateEntity.getDownload();
        if (entity != null) {
            entity.registerObserver(downloadListener);
        }
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();

        DownloadEntity entity = updateEntity.getDownload();
        if (entity != null) {
            entity.unregisterObserver(downloadListener);
        }
    }

    void updateItem() {

        CharSequence name = "下载并安装";
        boolean enable = true;
        boolean showProgress = false;

        {
            DownloadEntity downloadEntity = updateEntity.getDownload();

            int state = this.getState();
            switch (state) {
                case STATE_NONE: {
                    name = "下载并安装";
                    enable = true;
                    showProgress = false;
                    break;
                }
                case STATE_RUNNING: {
                    if (downloadEntity.getBytesRead() == 0) {
                        name = "下载并安装";
                        enable = false;
                        showProgress = true;
                    } else {
                        name = "正在下载...";
                        enable = false;
                        showProgress = false;
                    }
                    break;
                }
                case STATE_SUCESSFUL: {
                    name = "现在安装";
                    enable = true;
                    showProgress = false;
                    break;
                }
            }
        }

        {
            this.nameView.setText(name);
            this.itemView.setEnabled(enable);
            this.progressBar.setVisibility(showProgress? View.VISIBLE: View.INVISIBLE);
        }

    }

    int getState() {
        int state = STATE_NONE;

        DownloadEntity downloadEntity = updateEntity.getDownload();
        if (downloadEntity != null) {
            int status = downloadEntity.getStatus();
            if (status == DownloadListener.STATUS_SUCCESSFUL) {
                state = STATE_SUCESSFUL;
            } else if (status == DownloadListener.STATUS_RUNNING) {
                state = STATE_RUNNING;
            }
        }

        return state;
    }

    /**
     *
     */
    private class MyDownloadListener implements DownloadListener {

        boolean progressUpdate = false;

        @Override
        public void onStatusChanged(DownloadEntity entity, int status) {
            this.progressUpdate = false;

            updateItem();
        }

        @Override
        public void onProgressUpdate(DownloadEntity entity, long progress, long max, long speed) {
            if (!this.progressUpdate) {
                this.progressUpdate = true;

                updateItem();
            }
        }
    }
}
