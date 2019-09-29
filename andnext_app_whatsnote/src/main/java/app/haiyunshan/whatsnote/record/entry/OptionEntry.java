package app.haiyunshan.whatsnote.record.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Optional;

public class OptionEntry extends BaseEntry {

    @SerializedName("sort_id")
    String sortId;

    @SerializedName("sort_order")
    String sortOrder;

    @SerializedName("recent_sort_id")
    String recentSortId;

    @SerializedName("recent_sort_order")
    String recentSortOrder;

    @SerializedName("section_list")
    List<String> sectionList;

    public OptionEntry(String id) {
        super(id);
    }

    public Optional<String> getSortId() {
        return Optional.ofNullable(sortId);
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public Optional<String> getSortOrder() {
        return Optional.ofNullable(sortOrder);
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Optional<String> getRecentSortId() {
        return Optional.ofNullable(recentSortId);
    }

    public void setRecentSortId(String recentSortId) {
        this.recentSortId = recentSortId;
    }

    public Optional<String> getRecentSortOrder() {
        return Optional.ofNullable(recentSortOrder);
    }

    public void setRecentSortOrder(String recentSortOrder) {
        this.recentSortOrder = recentSortOrder;
    }

    public Optional<List<String>> getSectionList() {
        return Optional.ofNullable(sectionList);
    }

    public void setSectionList(List<String> sectionList) {
        this.sectionList = sectionList;
    }
}
