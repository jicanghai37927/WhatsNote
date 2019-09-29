package app.haiyunshan.whatsnote.article.entity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import androidx.collection.LruCache;
import app.haiyunshan.whatsnote.article.entry.*;
import app.haiyunshan.whatsnote.directory.Directory;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.UUIDUtils;
import com.google.gson.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;

class DocumentManager {

    static final String TYPE_ARTICLE = "";
    static final String TYPE_PICTURE = "picture";

    static final String URI_ARTICLE = "article.json";

    Deserializer deserializer;
    Factory factory;

    LruCache<String, Article> articleCache;

    Context context;

    private static DocumentManager instance;

    public static final DocumentManager getInstance(Context context) {
        if (instance == null) {
            instance = new DocumentManager(context);
        }

        return instance;
    }

    private DocumentManager(Context context) {
        this.context = context.getApplicationContext();

        this.articleCache = new LruCache<>(7);
    }

    Factory getFactory() {
        if (factory != null) {
            return factory;
        }

        {
            factory = new Factory();

            factory.put(ParagraphEntry.class, ParagraphEntity.class);
            factory.put(PictureEntry.class, PictureEntity.class);
            factory.put(FormulaEntry.class, FormulaEntity.class);
        }

        return factory;
    }

    Deserializer getDeserializer() {
        if (deserializer != null) {
            return deserializer;
        }

        {
            this.deserializer = new Deserializer();

            deserializer.put(ParagraphEntry.TYPE, ParagraphEntry.class);
            deserializer.put(PictureEntry.TYPE, PictureEntry.class);
            deserializer.put(FormulaEntry.TYPE, FormulaEntry.class);
        }

        return deserializer;
    }

    Document create(String id, String content) {
        Article article = articleCache.get(id);
        if (article == null) {
            article = createArticle(id, content);
            articleCache.put(id, article);
        }

        return new Document(context, id, article);
    }

    long save(Document document) {

        String id = document.getId();
        Article article = document.getArticle();

        articleCache.put(id, article);

        File file = getFile(id, TYPE_ARTICLE, URI_ARTICLE);
        file.getParentFile().mkdirs();

        GsonUtils.toJson(article, file);

        return 1;
    }

    void cache(Document document) {

        String id = document.getId();
        Article article = document.getArticle();

        articleCache.put(id, article);
    }

    Article createArticle(String id, String content) {
        Article ds = null;

        File file = getFile(id, TYPE_ARTICLE, URI_ARTICLE);
        if (file.exists()) {
            ds = GsonUtils.fromJson(file, Article.class, new Pair(ArticleEntry.class, this.getDeserializer()));
        }

        if (ds == null) {
            ds = new Article();
        }

        if (ds.size() == 0) {

            ParagraphEntry entry = new ParagraphEntry(UUIDUtils.next());
            entry.setText(content);

            ds.add(entry); // 默认一个段落
        }

        return ds;
    }

    File getFile(String id, String type, String uri) {
        File file;

        File dir = getDir(id);
        if (TextUtils.isEmpty(type)) {
            file = new File(dir, uri);
        } else {
            dir = new File(dir, type);
            file = new File(dir, uri);
        }

        return file;
    }

    File getDir(String id) {
        return getDir(context, id);
    }

    static File getDir(Context context, String id) {
        File dir = Directory.get(context, Directory.DIR_NOTE);
        dir = new File(dir, "files");
        dir = new File(dir, id + ".note");

        return dir;
    }

    /**
     *
     */
    private static class Deserializer implements JsonDeserializer<ArticleEntry> {

        HashMap<String, Class<? extends ArticleEntry>> map;

        Gson gson;

        Deserializer() {
            this.gson = new Gson();

            this.map = new HashMap<>();
        }

        void put(String type, Class<? extends ArticleEntry> clz) {
            map.put(type, clz);
        }

        @Override
        public ArticleEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ArticleEntry entry = null;

            String name = jsonObject.get("type").getAsString();
            Class<? extends ArticleEntry> clz = map.get(name);
            if (clz != null) {
                entry = gson.fromJson(json, clz);
            }

            if (entry == null) {
                throw new IllegalArgumentException("cannot find entry for " + jsonObject.toString());
            }

            return entry;
        }
    }

    /**
     *
     */
    static class Factory {

        HashMap<Class<? extends ArticleEntry>, Class<? extends DocumentEntity>> map;

        Factory() {

            this.map = new HashMap<>();

        }

        void put(Class<? extends ArticleEntry> entry, Class<? extends DocumentEntity> entity) {
            map.put(entry, entity);
        }

        DocumentEntity create(Document d, ArticleEntry entry) {
            Class<? extends DocumentEntity> entityClass = map.get(entry.getClass());
            if (entityClass == null) {
                throw new NotFoundException("Did't found entity for " + entry.getClass());
            }

            DocumentEntity entity = null;

            try {
                Constructor c = entityClass.getConstructor(d.getClass(), entry.getClass());
                c.setAccessible(true);

                entity = (DocumentEntity)c.newInstance(d, entry);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            if (entity == null) {
                throw new IllegalArgumentException("Cannot create instance for " + entityClass);
            }

            return entity;
        }
    }

    /**
     *
     */
    private static class NotFoundException extends RuntimeException {

        public NotFoundException(String message) {
            super(message);
        }
    }

}
