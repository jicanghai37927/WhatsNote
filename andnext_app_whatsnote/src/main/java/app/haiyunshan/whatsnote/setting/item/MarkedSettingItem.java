package app.haiyunshan.whatsnote.setting.item;

public class MarkedSettingItem extends BaseSettingItem {

    public static final int TYPE_PLAIN      = 0;
    public static final int TYPE_MARKDOWN   = 1;
    public static final int TYPE_HTML       = 2;

    String text;
    int type;

    public MarkedSettingItem(String text) {
        this(text, TYPE_MARKDOWN);
    }

    public MarkedSettingItem(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }
}
