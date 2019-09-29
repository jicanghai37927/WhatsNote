package app.haiyunshan.whatsnote.record.entry;

import club.andnext.base.BaseEntry;
import com.google.gson.annotations.SerializedName;

import java.util.Optional;

public class SavedStateEntry extends BaseEntry {

    @SerializedName("position")
    String offsetId;

    @SerializedName("top")
    String offsetTop;

    @SerializedName("offset")
    String textOffset;

    @SerializedName("focus")
    String focusId;

    @SerializedName("start")
    String selectionStart;

    @SerializedName("end")
    String selectionEnd;

    public SavedStateEntry(String id) {
        super(id);
    }

    public Optional<String> getOffsetId() {
        return Optional.ofNullable(offsetId);
    }

    public void setOffsetId(String offsetId) {
        this.offsetId = offsetId;
    }

    public Optional<String> getOffsetTop() {
        return Optional.ofNullable(offsetTop);
    }

    public void setOffsetTop(String offsetTop) {
        this.offsetTop = offsetTop;
    }

    public Optional<String> getTextOffset() {
        return Optional.ofNullable(textOffset);
    }

    public void setTextOffset(String textOffset) {
        this.textOffset = textOffset;
    }

    public Optional<String> getFocusId() {
        return Optional.ofNullable(focusId);
    }

    public void setFocusId(String focusId) {
        this.focusId = focusId;
    }

    public Optional<String> getSelectionStart() {
        return Optional.ofNullable(selectionStart);
    }

    public void setSelectionStart(String selectionStart) {
        this.selectionStart = selectionStart;
    }

    public Optional<String> getSelectionEnd() {
        return Optional.ofNullable(selectionEnd);
    }

    public void setSelectionEnd(String selectionEnd) {
        this.selectionEnd = selectionEnd;
    }
}
