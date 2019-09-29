package app.haiyunshan.whatsnote.setting.item;

import android.widget.Checkable;

import java.util.function.Consumer;

public class SwitchSettingItem extends BaseSettingItem<Boolean> implements Checkable {

    boolean checked;

    public SwitchSettingItem() {
        super();
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked());
    }

    @Override
    public Consumer<Boolean> getConsumer() {
        return super.getConsumer();
    }
}
