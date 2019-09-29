package app.haiyunshan.whatsnote.setting.viewholder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.setting.item.VersionSettingItem;
import app.haiyunshan.whatsnote.update.UpdateEntity;
import club.andnext.download.DownloadEntity;
import club.andnext.download.DownloadListener;

public class VersionSettingViewHolder extends BaseSettingViewHolder<VersionSettingItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_setting_version_list_item;

    static final long THRESHOLD_SIZE = 60 * 1024;

    TextView devView;
    ProgressBar progressBar;
    TextView sizeView;

    UpdateEntity updateEntity;
    DownloadListener downloadListener;

    @Keep
    public VersionSettingViewHolder(View itemView) {
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

        {
            view.setOnClickListener(null);
            view.setClickable(false);
            view.setEnabled(false);
        }

        {
            this.devView = view.findViewById(R.id.tv_developer);
            this.progressBar = view.findViewById(R.id.progress_horizontal);
            this.sizeView = view.findViewById(R.id.tv_size);
        }
    }

    @Override
    public void onBind(VersionSettingItem item, int position) {
        super.onBind(item, position);

        {
            UpdateEntity entity = item.getEntity();

            nameView.setText(String.format("%1$s %2$s", entity.getTitle(), entity.getName()));
            devView.setText(entity.getDeveloper());

            progressBar.setVisibility(isProgressVisible()? View.VISIBLE: View.GONE);
            sizeView.setText(getSize());
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

    CharSequence getSize() {
        long size = updateEntity.getSize();

        CharSequence text = formatSize(size);

        DownloadEntity downloadEntity = updateEntity.getDownload();
        if (downloadEntity != null) {
            int status = downloadEntity.getStatus();
            if (status == DownloadListener.STATUS_SUCCESSFUL) {
                text = "已下载";
            } else if (status == DownloadListener.STATUS_RUNNING) {
                if (downloadEntity.getBytesRead() > 0) {
                    text = "正在估算剩余时间...";
                }

                if (downloadEntity.getBytesRead() >= THRESHOLD_SIZE) {
                    long remain = (size - downloadEntity.getProgress());
                    long time = remain / downloadEntity.getSpeed();

                    text = formatDuration(time);
                }
            }
        }

        return text;
    }

    boolean isProgressVisible() {
        boolean visible = false;

        DownloadEntity downloadEntity = updateEntity.getDownload();
        if (downloadEntity != null) {
            int status = downloadEntity.getStatus();
            if (status == DownloadListener.STATUS_RUNNING) {
                visible = (downloadEntity.getBytesRead() >= THRESHOLD_SIZE);
            }
        }

        return visible;
    }

    static CharSequence formatDuration(long time) {
        StringBuilder sb = new StringBuilder();
        if (time > 0) {
            long seconds = time % 60;
            long minutes = time / 60 % 60;
            long hours = time / 60 / 60 % 24;
            long days = time / 60 / 60 / 24;

            if (days > 0) {
                sb.append(days);
                sb.append("天");
            } else if (hours > 0) {
                sb.append(hours);
                sb.append("小时");
            } else if (minutes > 0) {
                sb.append(minutes);
                sb.append("分种");
            } else if (seconds > 0) {
                sb.append(seconds);
                sb.append("秒");
            }
        }

        if (sb.length() == 0) {
            sb.append("1秒");
        }

        sb.insert(0, "还需约");
        return sb;
    }

    static CharSequence formatSize(long size) {
        StringBuilder sb = new StringBuilder();

        {
            long c = 1024;
            long b = 1024 * c;
            long a = 1024 * b;
            if (size > a) {
                sb.append(String.format("%.1f", size * 1.f / a));
                sb.append(" GB");
            } else if (size > b) {
                sb.append(String.format("%.1f", size * 1.f / b));
                sb.append(" MB");
            } else if (size > c) {
                sb.append(String.format("%.1f", size * 1.f / c));
                sb.append(" KB");
            } else {
                sb.append(size);
                sb.append(" bytes");
            }
        }

        return sb;
    }


    /**
     *
     */
    private class MyDownloadListener implements DownloadListener {

        long time = 0;

        @Override
        public void onStatusChanged(DownloadEntity entity, int status) {

            {
                this.time = System.currentTimeMillis();
            }

            {
                progressBar.setVisibility(isProgressVisible() ? View.VISIBLE : View.GONE);
                progressBar.setProgress((int)entity.getProgress());
                progressBar.setMax((int)(updateEntity.getSize()));

                sizeView.setText(getSize());
            }

        }

        @Override
        public void onProgressUpdate(DownloadEntity entity, long progress, long max, long speed) {

            {
                progressBar.setVisibility(isProgressVisible() ? View.VISIBLE : View.GONE);
                progressBar.setProgress((int) progress);
                progressBar.setMax((int)max);
            }

            long period = 6000;
            if (speed > 0) {
                if ((max - progress) / speed < 60) {
                    period = 1000;
                }
            }

            long now = System.currentTimeMillis();
            if (now - time >= period) {
                sizeView.setText(getSize());

                time = now;
            }
        }
    }
}
