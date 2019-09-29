package app.haiyunshan.whatsnote.record.entity;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.article.entity.Document;
import app.haiyunshan.whatsnote.record.database.RecordTable;
import app.haiyunshan.whatsnote.record.entry.RecordEntry;
import club.andnext.base.BaseEntity;
import club.andnext.utils.UUIDUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecordEntity extends AbstractEntitySet<RecordEntity, RecordEntry> {

    public static final String TYPE_FOLDER  = "folder";
    public static final String TYPE_NOTE    = "note";

    public static final String STYLE_ARTICLE    = "article";
    public static final String STYLE_CHAT       = "chat";

    public static final String ROOT_NOTE    = ".note";
    public static final String ROOT_TRASH   = ".trash";

    String id;
    String name;

    Long size;

    Boolean trashed;

    DateTime created;
    DateTime modified;
    DateTime deleted;
    DateTime published;

    RecordEntity(@NonNull Context context, @NonNull String id, @Nullable RecordEntry entry) {
        this(context, id, entry, null);
    }

    public RecordEntity(@NonNull Context context, @NonNull String id, @Nullable RecordEntry entry, @Nullable List<RecordEntity> list) {
        super(context, entry, list);

        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getStyle() {
        if (entry == null) {
            return "";
        }

        if (this.isDirectory()) {
            return "";
        }

        String style = entry.getStyle();
        style = (TextUtils.isEmpty(style))? STYLE_ARTICLE: style;
        return style;
    }

    public boolean isRoot() {
        return id.equals(ROOT_NOTE) || id.equals(ROOT_TRASH);
    }

    public String getParent() {
        return getEntry().map(e -> e.getParent()).orElse("");
    }

    public RecordEntity getParentEntity() {
        String id = this.getParent();
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        return RecordEntity.create(id).orElse(null);
    }

    public boolean setParent(RecordEntity entity) {
        if (entry == null) {
            return false;
        }

        if (entity.isDescendantOf(this.getId())) {
            return false;
        }

        String parentId = entity.getId();
        if (this.getParent().equals(parentId)) {
            return false;
        }

        if (parentId.equals(ROOT_NOTE) || parentId.equals(ROOT_TRASH)) {

        } else {
            Optional<RecordEntry> target = getManager().obtainTable(RecordTable.class).get(parentId);
            parentId = target.map(e -> e.getId()).orElse("");
        }

        if (TextUtils.isEmpty(parentId)) {
            return false;
        }

        // set name
        String name = RecordUtils.getName(context, entry, parentId, false);
        if (this.isAlias()) {
            entry.setAlias(name);
        } else {
            entry.setName(name);
        }

        // set parent
        entry.setParent(parentId);

        return true;
    }

    public Optional<RecordEntity> findDirectoryByName(String name) {
        return getList().stream()
                .filter(e -> e.isDirectory())
                .filter(e -> RecordUtils.getName(e.entry).equals(name))
                .findAny();
    }

    public Optional<RecordEntity> findNoteByName(String style, String name) {
        return getList().stream()
                .filter(e -> !e.isDirectory())
                .filter(e -> e.getStyle().equals(style))
                .filter(e -> RecordUtils.getName(e.entry).equals(name))
                .findAny();
    }

    public String getName() {
        if (this.name != null) {
            return name;
        }

        this.name = RecordUtils.getName(entry);
        return this.name;
    }

    public String getAlias(String name) {
        if (RecordUtils.getName(entry).equals(name)) {
            return name;
        }

        String parentId = this.getParent();
        if (!RecordUtils.findByName(context, parentId, entry.getId(), name).isPresent()) {
            return name;
        }

        int count = 2;
        while (true) {
            String text = name + " " + count;
            if (!RecordUtils.findByName(context, parentId, entry.getId(), text).isPresent()) {
                return text;
            }

            ++count;
        }
    }

    public void setName(final String name) {
        this.name = (name == null)? "": name;

        getEntry().ifPresent(e -> e.setName(name));
    }

    public void setAlias(final String name) {
        getEntry().ifPresent(e -> e.setAlias(name));
    }

    public boolean isAlias() {
        if (entry == null) {
            return false;
        }

        return TextUtils.isEmpty(entry.getName());
    }

    public long getSize() {
        if (this.size != null) {
            return size;
        }

        this.size = 0L;

        if (entry != null) {
            boolean dir = this.isDirectory();
            if (dir) {
                this.size = getManager().obtainTable(RecordTable.class).getList().stream()
                        .filter(e -> e.getParent().equals(this.id))
                        .count();
            } else {
                this.size = Long.parseLong(entry.getSize());
            }
        }

        return this.size;
    }

    public void setSize(long size) {
        this.size = size;

        getEntry().ifPresent(e -> e.setSize(size));
    }

    public boolean isDirectory() {
        return getEntry().map(e -> e.getType().equals(TYPE_FOLDER)).orElse(true);
    }

    public boolean isTrash() {
        if (this.trashed != null) {
            return this.trashed;
        }

        this.trashed =  isDescendantOf(ROOT_TRASH);
        return trashed;
    }

    public boolean isDescendantOf(String id) {
        RecordTable table = getManager().obtainTable(RecordTable.class);
        if (entry == null) {
            return this.getId().equals(id);
        }

        return RecordUtils.isDescendantOf(table, entry, id);
    }

    public File getSnapshot() {
        return getManager().getSnapshot(this.getId());
    }

    public File getFile() {
        if (this.isDirectory()) {
            return null;
        }

        String style = this.getStyle();

        return Document.getFile(context, this.getId());
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append("snapshot://");
        sb.append(this.getId());
        sb.append("?modified=" + this.getModified().getMillis());
        return sb.toString();
    }

    public DateTime getCreated() {
        if (created != null) {
            return created;
        }

        return getEntry()
                .map(e -> entry.getCreated())
                .map(e -> {
                    created = DateTime.parse(e);
                    return created;
                })
                .orElseGet(() -> {
                    created = DateTime.now();
                    return created;
                });
    }

    public void setCreated(DateTime time) {
        getEntry().ifPresent(e -> {
            this.created = time;

            e.setCreated(created.toString());
        });
    }

    public DateTime getModified() {

        if (modified != null) {
            return modified;
        }

        return getEntry()
                .map(e -> entry.getModified())
                .map(e -> {
                    modified = DateTime.parse(e);
                    return modified;
                })
                .orElseGet(() -> {
                    modified = DateTime.now();
                    return modified;
        });
    }

    public void setModified(final DateTime time) {
        getEntry().ifPresent(e -> {
            this.modified = time;
            e.setModified(modified.toString());
        });
    }

    public Optional<DateTime> getDeleted() {

        if (deleted != null) {
            return Optional.ofNullable(deleted);
        }

        Optional<DateTime> optional = getEntry()
                .map(e -> entry.getDeleted())
                .map(e -> e.map(s -> DateTime.parse(s)).orElse(null));

        this.deleted = optional.orElse(null);

        return optional;
    }

    public void setDeleted(final DateTime time) {
        getEntry().ifPresent(e -> {
            this.deleted = time;

            String text = (time == null)? null: time.toString();
            e.setDeleted(text);
        });
    }

    public Optional<DateTime> getPublished() {

        if (published != null) {
            return Optional.ofNullable(published);
        }

        Optional<DateTime> optional = getEntry()
                .map(e -> entry.getPublished())
                .map(e -> e.map(s -> DateTime.parse(s)).orElse(null));

        this.published = optional.orElse(null);

        return optional;
    }

    public void setPublished(final DateTime time) {
        getEntry().ifPresent(e -> {
            this.published = time;

            String text = (time == null)? null: time.toString();
            e.setDeleted(text);
        });
    }

    public List<String> getTagList() {
        return this.entry.getTagList();
    }

    public boolean trash() {
        if (this.isTrash()) {
            return false;
        }

        if (entry == null) {
            return false;
        }

        String parentId = ROOT_TRASH;
        if (this.getParent().equals(parentId)) {
            return false;
        }

        // set deleted now
        this.setDeleted(DateTime.now());

        // set name
        String name = RecordUtils.getName(context, entry, parentId, true);
        if (this.isAlias()) {
            entry.setAlias(name);
        } else {
            entry.setName(name);
        }

        // set ancestor for recover
        entry.setAncestor(entry.getParent());

        // set parent
        entry.setParent(parentId);

        return true;
    }

    public boolean recover() {
        if (!this.isTrash()) {
            return true;
        }

        if (entry == null) {
            return true;
        }

        String parentId = entry.getAncestor().orElse("");

        // check parent exist or not
        if (!TextUtils.isEmpty(parentId)) {
            if (!parentId.equals(ROOT_NOTE)) {
                RecordEntity parent = RecordEntity.create(parentId).orElse(null);
                parentId = (parent == null || parent.isTrash())? "": parent.getId();
            }
        }

        // if parent is note exist, we move record to root
        if (TextUtils.isEmpty(parentId)) {
            parentId = ROOT_NOTE;
        }

        // set deleted null
        this.setDeleted(null);

        // set name
        String name = RecordUtils.getName(context, entry, parentId, true);
        if (this.isAlias()) {
            entry.setAlias(name);
        } else {
            entry.setName(name);
        }

        // set ancestor null
        entry.setAncestor(null);

        // set parent
        entry.setParent(parentId);

        return true;

    }

    public List<RecordEntity> delete() {
        final RecordTable table = getManager().obtainTable(RecordTable.class);
        if (!this.isDirectory()) {

            // remove data
            table.remove(this.entry);

            // to entity
            return Arrays.asList(this);

        } else {

            // filter descendant of this
            final String parentId = this.getId();
            List<RecordEntry> list = table.getList().stream()
                    .filter(e -> RecordUtils.isDescendantOf(table, e, parentId))
                    .collect(Collectors.toList());

            // remove data
            for (RecordEntry e : list) {
                table.remove(e);
            }

            // to entity
            return list.stream()
                    .map(e -> new RecordEntity(getContext(), e.getId(), e))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean areContentsTheSame(BaseEntity obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RecordEntity)) {
            return false;
        }

        RecordEntity another = (RecordEntity)obj;

        if (!this.getId().equals(another.getId())) {
            return false;
        }

        boolean result = true;

        if (result) {
            result = this.getName().equals(another.getName());
        }

        if (result) {
            result = this.getSize() == another.getSize();
        }

        if (result) {
            result = this.getCreated().equals(another.getCreated());
        }

        if (result) {
            result = (this.getModified().equals(another.getModified()));
        }

        if (result) {
            result = !(this.isTrash() ^ another.isTrash());
        }

        return result;
    }

    public long save() {
        return getManager().save();
    }

    public static RecordEntity listAll(String id) {
        return Factory.listAll(WhatsApp.getInstance(), id);
    }

    public static RecordEntity listFolder(String id) {
        return Factory.listFolder(WhatsApp.getInstance(), id);
    }

    public static RecordEntity list(String... ids) {
        return Factory.list(WhatsApp.getInstance(), ids);
    }

    public static RecordEntity create(String parent, String name, String type, String style) {
        return Factory.create(WhatsApp.getInstance(), parent, name, type, style);
    }

    public static Optional<RecordEntity> create(String id) {
        return Factory.create(WhatsApp.getInstance(), id);
    }

    /**
     *
     */
    static class Factory {

        static RecordEntity listAll(final Context context, final String id) {
            RecordManager mgr = RecordManager.getInstance(context);
            RecordTable ds = mgr.obtainTable(RecordTable.class);

            Optional<RecordEntry> entry = ds.get(id);

            List<RecordEntity> list = ds.getList().stream()
                    .filter(e -> e.getParent().equals(id))
                    .map(e -> new RecordEntity(context, e.getId(), e))
                    .collect(Collectors.toList());


            RecordEntity entity = new RecordEntity(context, id, entry.orElse(null), list);
            return entity;
        }


        static RecordEntity listFolder(final Context context, final String id) {
            RecordManager mgr = RecordManager.getInstance(context);
            RecordTable ds = mgr.obtainTable(RecordTable.class);

            Optional<RecordEntry> entry = ds.get(id);

            List<RecordEntity> list = ds.getList().stream()
                    .filter(e -> e.getParent().equals(id))
                    .filter(e -> e.getType().equals(RecordEntity.TYPE_FOLDER))
                    .map(e -> new RecordEntity(context, e.getId(), e))
                    .collect(Collectors.toList());


            RecordEntity entity = new RecordEntity(context, id, entry.orElse(null), list);
            return entity;
        }

        static RecordEntity list(final Context context, String... ids) {
            List<String> array = Arrays.asList(ids);

            return list(context, array);
        }

        static RecordEntity list(final Context context, List<String> array) {
            RecordManager mgr = RecordManager.getInstance(context);
            RecordTable ds = mgr.obtainTable(RecordTable.class);

            List<RecordEntity> list = ds.getList().stream()
                    .filter(e -> array.contains(e.getId()))
                    .map(e -> new RecordEntity(context, e.getId(), e))
                    .filter(e -> !e.isTrash())
                    .collect(Collectors.toList());

            RecordEntity entity = new RecordEntity(context, "", null, list);
            return entity;
        }

        static RecordEntity create(final Context context, String parent, String name, String type, String style) {
            RecordEntry entry = new RecordEntry(UUIDUtils.next(), parent, type, style, DateTime.now().toString());
            entry.setAlias(name);

            RecordManager mgr = RecordManager.getInstance(context);
            RecordTable ds = mgr.obtainTable(RecordTable.class);
            ds.add(entry);

            return new RecordEntity(context, entry.getId(), entry);
        }

        static Optional<RecordEntity> create(final Context context, String id) {
            RecordManager mgr = RecordManager.getInstance(context);
            RecordTable ds = mgr.obtainTable(RecordTable.class);

            return ds.getList().stream()
                    .filter(e -> e.getId().equals(id))
                    .map(e -> new RecordEntity(context, e.getId(), e))
                    .findAny();
        }

    }

}
