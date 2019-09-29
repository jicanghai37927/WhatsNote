package club.andnext.download;

import android.content.Context;
import club.andnext.base.BaseEntitySet;
import club.andnext.utils.FileUtils;
import club.andnext.utils.GsonUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadAgent extends BaseEntitySet<DownloadEntity, DownloadEntry> implements Interceptor {

    OkHttpClient httpClient;
    File file;

    public static final DownloadAgent create(Context context, OkHttpClient httpClient, File file) {
        DownloadAgent agent = new DownloadAgent(context, httpClient, file);
        agent.load();

        return agent;
    }

    DownloadAgent(Context context, OkHttpClient httpClient, File file) {
        super(context, null);

        this.httpClient = httpClient;
        this.file = file;

        this.list = new ArrayList<>();
    }

    OkHttpClient getHttpClient() {
        return this.httpClient;
    }

    public void load() {
        this.list.clear();

        DownloadTable table = GsonUtils.fromJson(file, DownloadTable.class);
        if (table != null) {
            List<DownloadEntity> array = table.getList().stream()
                    .map(e -> new DownloadEntity(this, e))
                    .collect(Collectors.toList());

            this.list.addAll(array);
        }
    }

    public DownloadEntity enqueue(DownloadRequest request) {
        DownloadEntry entry = new DownloadEntry(request);
        DownloadEntity entity = new DownloadEntity(this, entry);
        entity.registerObserver(request.listener);

        super.add(entity);

        entity.start();
        return entity;
    }

    public int start(DownloadEntity entity) {
        entity.start();

        return entity.getStatus();
    }

    public void delete(String id, boolean removeDestination) {
        DownloadEntity entity = this.remove(id);
        if (entity == null) {
            return;
        }

        // stop download
        {
            entity.stop();
        }

        // delete download file
        try {
            File file = entity.getDownloadFile();
            FileUtils.forceDelete(file);
        } catch (IOException e) {

        }

        // delete destination file
        if (removeDestination) {
            try {
                File file = entity.getDestinationFile();
                FileUtils.forceDelete(file);
            } catch (IOException e) {

            }
        }
    }

    public void save() {
        DownloadTable table = new DownloadTable();

        this.list.stream()
                .map(e -> e.getEntry().get())
                .forEach(e -> table.add(e));

        GsonUtils.toJson(table, file);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        for (DownloadEntity e : this.list) {
            response = e.getInterceptor().intercept(chain);
            if (response != null) {
                break;
            }
        }

        return response;
    }

    public DownloadEntity findBySource(String source) {
        return this.getList().stream()
                .filter(e -> e.getSource().equals(source))
                .findFirst()
                .orElse(null);
    }
}
