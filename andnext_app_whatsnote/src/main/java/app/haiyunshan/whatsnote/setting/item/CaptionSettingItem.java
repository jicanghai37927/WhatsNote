package app.haiyunshan.whatsnote.setting.item;

import app.haiyunshan.whatsnote.R;

public class CaptionSettingItem extends BaseSettingItem {

    int appearance;

    int minLines;

    int paddingTop = -1;
    int paddingBottom = -1;

    public CaptionSettingItem() {
        this(R.style.TextAppearance_AppCompat_Menu);
    }

    public CaptionSettingItem(int appearance) {
        this.appearance = appearance;

        this.minLines = 7;
        this.dividerVisible = false;
    }

    public int getAppearance() {
        return appearance;
    }

    public void setAppearance(int appearance) {
        this.appearance = appearance;
    }

    public int getMinLines() {
        return minLines;
    }

    public void setMinLines(int minLines) {
        this.minLines = minLines;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }
}
