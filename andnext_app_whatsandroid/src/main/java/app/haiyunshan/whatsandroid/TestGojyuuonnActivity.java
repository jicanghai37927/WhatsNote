package app.haiyunshan.whatsandroid;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.utils.AssetUtils;
import club.andnext.utils.FileUtils;
import club.andnext.utils.GsonUtils;
import com.google.gson.Gson;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestGojyuuonnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gojyuuonn);
    }

    void onDownloadClick(View view) {
        view.setEnabled(false);

        this.download();
    }

    void download() {
        List<Entity> list = createList();
        Log.w("AA", "entity size = " + list.size());
        for (Entity e : list) {
            Log.w("AA", "name = " + e.getName());
            Log.w("AA", "video = " + e.getVideo());
        }

        this.download(list);
    }

    void download(List<Entity> list) {

        new Thread(() -> {

            File dir = Environment.getExternalStorageDirectory();
            dir = new File(dir, "Gojyuuonn_Downloads");
            dir.mkdirs();

            for (int i = 0, size = list.size(); i < size; i++) {
                Entity e = list.get(i);
                String url = e.getVideo();
                String name = e.getName();
                String tmp = name + ".tmp";

                if (new File(dir, name).exists()) {
                    continue;
                }

                boolean result = download(url, dir.getAbsolutePath(), tmp);
                if (result) {
                    result = new File(dir, tmp).renameTo(new File(dir, name));
                }

                Log.w("AA", (i + 1) + "/" + size + " " + name + ": " + (result? "下载成功": "下载失败"));

                try {
                    Thread.sleep(11 * 1000);
                } catch (Exception e1) {

                }
            }

            Log.w("AA", "全部下载完成");
        }).start();
    }

    List<Entity> createList() {
        String text = AssetUtils.getString(this, "gojyuuonn/gojyuuonn-levels_test.json");
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        List<Test> list = jsonToList(text, Test[].class);
        Log.w("AA", "test size = " + list.size());

        ArrayList<Entity> array = new ArrayList<>();

        int position = 1;
        for (Test test : list) {
            test.position = position;
            if (!TextUtils.isEmpty(test.video)) {
                array.add(new Entity(test, null));
            }

            int index = 1;
            for (Tab tab: test.tabs) {
                tab.position = index;

                if (!TextUtils.isEmpty(tab.video)) {
                    array.add(new Entity(test, tab));
                }

                ++index;
            }

            ++position;
        }

        return array;
    }

    public static <T> List<T> jsonToList(String json, Class<T[]> clazz)
    {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }

    public static boolean download(String url, String saveDir, String fileName) {

        boolean result = true;

        BufferedOutputStream bos = null;
        InputStream is = null;
        try {
            byte[] buff = new byte[100 * 1024];
            is = new URL(url).openStream();
            File file = new File(saveDir, fileName);
            file.getParentFile().mkdirs();
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int count = 0;
            while ( (count = is.read(buff)) != -1) {
                bos.write(buff, 0, count);
            }
        }
        catch (IOException e) {
            e.printStackTrace();

            result = false;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return result;

    }

    private class Entity {
        public Test test;
        public Tab tab;

        public Entity(Test test, Tab tab) {
            this.test = test;
            this.tab = tab;
        }

        public String getName() {
            StringBuilder name = new StringBuilder();

            if (tab == null) {
                name.append(test.position);
                name.append("-");

                name.append(0);
                name.append("-");

                name.append(test.title);
                name.append("-");

                name.append(test.category);

                if (!test.category.equalsIgnoreCase(test.subtitle)) {
                    name.append("-");
                    name.append(test.subtitle);
                }

            } else {
                name.append(test.position);
                name.append("-");

                name.append(tab.position);
                name.append("-");

                name.append(tab.title);
            }

            name.append(".mp4");
            return name.toString();
        }

        public String getVideo() {
            String video;

            if (tab == null) {
                video = test.video;
            } else {
                video = tab.video;
            }

            return video;
        }
    }

    /**
     *
     */
    private class Test {

        public transient int position;

        public String title;
        public String video;
        public String category;
        public String subtitle;

        public List<Tab> tabs;
    }

    /**
     *
     */
    private class Tab {

        public transient int position;

        public String title;
        public String video;

    }


}


