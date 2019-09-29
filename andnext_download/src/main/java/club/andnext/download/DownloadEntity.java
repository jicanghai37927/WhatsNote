package club.andnext.download;

import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import club.andnext.base.BaseEntity;
import club.andnext.helper.ObservableHelper;
import club.andnext.utils.FileUtils;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

import java.io.*;

public class DownloadEntity extends BaseEntity<DownloadEntry> {

    static final int MSG_STATUS_CHANGED     = 1;
    static final int MSG_PROGRESS_UPDATE    = 2;

    Call call;
    DownloadCallback callback;

    int status;

    long startSize; // local file size

    long progress;  // download size
    long speed;     // download speed, bytes per second
    long contentLength; // total size

    long period = 100; // progress update period

    DownloadObservable observable;
    DownloadInterceptor interceptor;

    DownloadAgent parent;

    DownloadEntity(@NonNull DownloadAgent agent, @NonNull DownloadEntry entry) {
        super(agent.getContext(), entry);

        this.parent = agent;

        this.observable = new DownloadObservable();
        this.interceptor = new DownloadInterceptor();

        this.status = DownloadListener.STATUS_PAUSED;
        if (getDestinationFile().exists()) {
            this.status = DownloadListener.STATUS_SUCCESSFUL;
        }
    }

    public int getStatus() {
        if (this.status == DownloadListener.STATUS_SUCCESSFUL) {
            if (!getDestinationFile().exists()) {
                this.status = DownloadListener.STATUS_PAUSED;
            }
        }

        return status;
    }

    public long getProgress() {
        if (call != null) {
            return startSize + progress;
        }

        File file = getDestinationFile();
        if (file.exists()) {
            return file.length();
        }

        file = getDownloadFile();
        if (file.exists()) {
            return file.length();
        }

        return 0;

    }

    public long getBytesRead() {
        return this.progress;
    }

    public long getSpeed() {
        return this.speed;
    }

    public String getSource() {
        return entry.getSource();
    }

    public void registerObserver(DownloadListener listener) {
        observable.registerObserver(listener);
    }

    public void unregisterObserver(DownloadListener listener) {
        observable.unregisterObserver(listener);
    }

    public void unregisterAll() {
        observable.unregisterAll();
    }

    void start() {

        // if destination exist, successful
        if (getDestinationFile().exists()) {
            this.status = DownloadListener.STATUS_SUCCESSFUL;
            getObservable().onStatusChanged(status);
            return;
        }

        // check download file size, if larger than length, rename to destination
        File file = getDownloadFile();
        if (entry.getLength() >= 0 && file.exists() && file.length() >= entry.getLength()) {
            File dest = getDestinationFile();
            if (dest.exists()) {
                try {
                    FileUtils.forceDelete(dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            boolean result = file.renameTo(dest);
            if (result) {
                this.status = DownloadListener.STATUS_SUCCESSFUL;
                getObservable().onStatusChanged(status);
                return;
            }
        }

        // connect to remote server
        {
            // prepare
            this.startSize = (file.exists()) ? file.length() : 0;
            this.progress = 0;
            this.speed = 0;
            this.contentLength = 0;

            // create request
            Request.Builder builder = new Request.Builder();
            builder.url(entry.getSource());
            if (file.exists() && startSize > 0) { // 断点续传
                builder.header("RANGE", "bytes=" + startSize + "-");
            }

            // send request
            Request request = builder.build();

            this.call = parent.getHttpClient().newCall(request);
            this.callback = new DownloadCallback();

            call.enqueue(callback);

            // notify
            this.status = DownloadListener.STATUS_RUNNING;
            getObservable().onStatusChanged(status);
        }
    }

    void stop() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        if (callback != null) {
            callback.close();
        }

        this.call = null;
        this.callback = null;
    }

    DownloadObservable getObservable() {
        return this.observable;
    }

    DownloadInterceptor getInterceptor() {
        return interceptor;
    }

    File getDownloadFile() {
        return new File(entry.getDestination() + ".whatsdownload");
    }

    File getDestinationFile() {
        return new File(entry.getDestination());
    }

    /**
     *
     */
    class DownloadObservable extends ObservableHelper<DownloadListener> implements Handler.Callback {

        Handler handler;

        long timestamp;
        long length;

        DownloadEntity parent;

        DownloadObservable() {
            this.handler = new Handler(this);

            this.timestamp = -1;
            this.length = -1;

            this.parent = DownloadEntity.this;
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STATUS_CHANGED: {
                    onStatusChanged(msg.arg1);
                    break;
                }
                case MSG_PROGRESS_UPDATE: {
                    onProgressUpdate(msg.arg1, msg.arg2);
                    break;
                }
            }
            return true;
        }

        void notifyStatusChanged(int status) {
            Message msg = handler.obtainMessage();
            msg.what = MSG_STATUS_CHANGED;
            msg.arg1 = status;

            handler.sendMessage(msg);
        }

        void notifyProgressUpdate(long size) {
            progress += (size > 0)? size: 0;

            long now = System.currentTimeMillis();
            if ((size < 0) || (now - timestamp >= period)) {
                long speed = 0;
                if (this.length > 0 && this.timestamp > 0) {
                    long diff = progress - length;
                    long time = now - timestamp;
                    if (diff > 0 && time > 0) {
                        speed = diff * 1000 / time; // bytes per second
                    }
                }

                Message msg = handler.obtainMessage();
                msg.what = MSG_PROGRESS_UPDATE;
                msg.arg1 = (int)progress;
                msg.arg2 = (int)speed;

                handler.sendMessage(msg);

                this.timestamp = now;
                this.length = progress;
            }

        }

        void onStatusChanged(int status) {
            for (DownloadListener l : mObservers) {
                l.onStatusChanged(DownloadEntity.this, status);
            }
        }

        void onProgressUpdate(long progress, long speed) {
            long max = startSize + contentLength;
            long current = startSize + progress;

            if (parent.speed == 0) {
                parent.speed = speed;
            } else {
                parent.speed = (parent.speed + speed) / 2;
            }

            for (DownloadListener l : mObservers) {
                l.onProgressUpdate(DownloadEntity.this, current, max, speed);
            }
        }
    }

    /**
     *
     */
    class DownloadCallback implements Callback {

        // 保存文件到本地
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;

        @Override
        public void onFailure(Call call, IOException e) {

            // local failure, cannot connect to remote server

            // notify fail
            stop();
            status = DownloadListener.STATUS_FAILED;
            getObservable().notifyStatusChanged(status);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {

                // notify fail
                stop();
                status = DownloadListener.STATUS_FAILED;
                getObservable().notifyStatusChanged(status);

                return;
            }

            long length = response.body().contentLength();

            if (length == 0) {

                File dest = getDestinationFile();
                File download = getDownloadFile();
                if (download.exists() && !dest.exists()) {
                    download.renameTo(dest);
                }

            } else {

                byte[] buff = new byte[2048];
                int len;
                try {

                    is = response.body().byteStream();
                    bis = new BufferedInputStream(is);

                    File file = getDownloadFile();

                    // 随机访问文件，可以指定断点续传的起始位置
                    randomAccessFile = new RandomAccessFile(file, "rwd");
                    randomAccessFile.seek(startSize);
                    while ((len = bis.read(buff)) != -1) {
                        randomAccessFile.write(buff, 0, len);
                    }

                    // close stream
                    this.close();

                    // rename to destination
                    File dest = getDestinationFile();
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest);
                    }
                    file.renameTo(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    this.close();
                }
            }

            // notify result
            stop();

            boolean result = getDestinationFile().exists();
            status = result? DownloadListener.STATUS_SUCCESSFUL: DownloadListener.STATUS_FAILED;
            getObservable().notifyStatusChanged(status);
        }

        void close() {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }

                is = null;
            }

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {

                }

                bis = null;
            }

            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {

                }

                randomAccessFile = null;
            }
        }
    }

    /**
     *
     */
    class DownloadInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Call c = chain.call();
            if (c != call) {
                return null;
            }

            Response response = chain.proceed(chain.request());
            if (response.isSuccessful()) {
                long length = response.body().contentLength();

                {
                    contentLength = length;
                }

                if (!getDownloadFile().exists() && entry.getLength() < 0) {
                    entry.setLength(length);
                    parent.save();
                }
            }

            return response.newBuilder()
                    .body(new DownloadResponseBody(response))
                    .build();
        }
    }

    /**
     *
     */
    class DownloadResponseBody extends ResponseBody {

        private Response response;

        public DownloadResponseBody(Response response) {
            this.response = response;
        }

        @Override
        public MediaType contentType() {
            return response.body().contentType();
        }

        @Override
        public long contentLength() {
            return response.body().contentLength();
        }

        @Override
        public BufferedSource source() {

            return Okio.buffer(new ForwardingSource(response.body().source()) {

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long size = super.read(sink, byteCount);

                    {
                        getObservable().notifyProgressUpdate(size);
                    }

                    return size;
                }
            });
        }

    }
}
