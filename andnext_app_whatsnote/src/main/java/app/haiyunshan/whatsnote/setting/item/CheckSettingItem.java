package app.haiyunshan.whatsnote.setting.item;

import java.util.function.IntFunction;

public class CheckSettingItem extends BaseSettingItem<Integer> {

    IntFunction<Boolean> transform;

    public CheckSettingItem(IntFunction<Boolean> transform) {
        super();

        this.transform = transform;
    }

    public boolean isChecked() {
        if (transform != null) {
            return transform.apply(this.getUserObject());
        }

        return false;
    }
}
