package app.haiyunshan.whatsnote.setting.item;

import java.util.function.IntFunction;

public class NumericalSettingItem extends BaseSettingItem<Integer> {

    int value;
    int minValue;
    int maxValue;
    int step;

    IntFunction<CharSequence> transform;

    public NumericalSettingItem() {
        this(null);
    }

    public NumericalSettingItem(IntFunction<CharSequence> transform) {
        this(68, 1, 100, 1, transform);
    }

    public NumericalSettingItem(int value, int min, int max, int step, IntFunction<CharSequence> transform) {
        super();

        this.setNumerical(min, max, step);
        this.setValue(value);
    }

    public void setTransform(IntFunction<CharSequence> transform) {
        this.transform = transform;
    }

    public IntFunction<CharSequence> getTransform() {
        return this.transform;
    }

    public void setNumerical(int min, int max, int step) {
        this.minValue = Math.min(min, max);
        this.maxValue = Math.max(min, max);
        this.step = Math.abs(step);
    }

    public void setValue(int value) {
        int v = value;
        v = Math.max(v, minValue);
        v = Math.min(v, maxValue);

        this.value = v;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isMin() {
        return value <= this.minValue;
    }

    public boolean isMax() {
        return value >= this.maxValue;
    }

    public boolean increase() {
        this.value += step;
        this.value = Math.min(value, maxValue);

        return isMax();
    }

    public boolean decrease() {
        this.value -= step;
        this.value = Math.max(value, minValue);

        return isMin();
    }

    public Integer getUserObject() {
        return value;
    }

}
