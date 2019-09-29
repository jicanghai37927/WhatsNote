package app.haiyunshan.whatsnote.record.entry;

import android.text.TextUtils;
import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecordEntry extends BaseEntry {

    @SerializedName("type")
    String type;

    @SerializedName("style")
    String style;

    @SerializedName("parent")
    String parent;

    @SerializedName("ancestor")
    String ancestor; // when trash, set ancestor for recover

    @SerializedName("name")
    String name; // real name from user

    @SerializedName("alias")
    String alias; // inner use, alias from app, name first

    @SerializedName("desc")
    String desc; // entity description

    @SerializedName("size")
    String size;

    @SerializedName("tag_list")
    List<String> tagList;

    @SerializedName("created")
    String created;

    @SerializedName("modified")
    String modified;

    @SerializedName("deleted")
    String deleted;

    @SerializedName("published")
    String published;

    public RecordEntry(String id, String parent, String type, String created) {
        this(id, parent, type, null, created);
    }

    public RecordEntry(String id, String parent, String type, String style, String created) {
        super(id);

        this.parent = parent;
        this.type = type;
        this.style = style;

        this.created = created;
        this.modified = created;
    }

    public String getType() {
        return type;
    }

    public String getStyle() {
        return style;
    }

    public String getParent() {
        return parent == null? "": parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Optional<String> getAncestor() {
        return Optional.ofNullable(ancestor);
    }

    public void setAncestor(String ancestor) {
        this.ancestor = ancestor;
    }

    public String getName() {
        return name == null? "": name;
    }

    public RecordEntry setName(String name) {
        this.name = name;
        return this;
    }

    public String getAlias() {
        return alias == null? "": alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDesc() {
        return desc == null? "": desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSize() {
        return TextUtils.isEmpty(size)? "0": size;
    }

    public void setSize(long size) {
        this.size = String.valueOf(size);
    }

    public List<String> getTagList() {
        if (tagList == null) {
            this.tagList = new ArrayList<>();
        }

        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String utc) {
        this.created = utc;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String utc) {
        this.modified = utc;
    }

    public Optional<String> getDeleted() {
        return Optional.ofNullable(deleted);
    }

    public void setDeleted(String utc) {
        this.deleted = utc;
    }

    public Optional<String> getPublished() {
        return Optional.ofNullable(published);
    }

    public void setPublished(String utc) {
        this.published = utc;
    }


}