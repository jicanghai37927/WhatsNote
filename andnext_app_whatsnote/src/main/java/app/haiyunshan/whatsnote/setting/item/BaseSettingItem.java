package app.haiyunshan.whatsnote.setting.item;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseSettingItem<T> {

    public static final int DIVIDER_ITEM = 0;
    public static final int DIVIDER_NAME = 1;

    String id;

    @DrawableRes int iconResId;
    ColorStateList iconTintList;

    ColorStateList buttonColor;

    CharSequence name;

    CharSequence hint;
    Supplier<CharSequence> hintSupplier;

    int badge;
    Supplier<Integer> badgeSupplier;

    int chevron;

    Consumer<T> consumer;

    T userObject;

    boolean dividerVisible;
    int dividerType;

    public BaseSettingItem() {
        this("");
    }

    public BaseSettingItem(CharSequence name) {
        this(name, null);
    }

    public BaseSettingItem(CharSequence name, Consumer<T> consumer) {
        this(name, consumer, null);
    }

    public BaseSettingItem(CharSequence name, Consumer<T> consumer, T userObject) {
        this("", 0, name, View.INVISIBLE, consumer, userObject);
    }

    public BaseSettingItem(String id, int iconResId, CharSequence name, int chevron, Consumer<T> consumer, T userObject) {
        this.id = id;
        this.iconResId = iconResId;
        this.name = name;
        this.chevron = chevron;
        this.consumer = consumer;
        this.userObject = userObject;

        this.dividerVisible = true;
        this.dividerType = DIVIDER_ITEM;
    }

    public String getId() {
        return this.id;
    }

    public BaseSettingItem<T> setId(String id) {
        this.id = id;

        return this;
    }

    public int getIcon() {
        return this.iconResId;
    }

    public BaseSettingItem<T> setIcon(int iconResId) {
        this.iconResId = iconResId;

        return this;
    }

    public ColorStateList getIconTintList() {
        return iconTintList;
    }

    public BaseSettingItem<T> setIconTintList(Context context, @ColorRes int colorResId) {
        this.iconTintList = ColorStateList.valueOf(context.getColor(colorResId));

        return this;
    }

    public ColorStateList getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(ColorStateList buttonColor) {
        this.buttonColor = buttonColor;
    }

    public CharSequence getName() {
        return this.name;
    }

    public BaseSettingItem<T> setName(CharSequence name) {
        this.name = name;

        return this;
    }

    public CharSequence getHint() {
        return this.hint;
    }

    public BaseSettingItem<T> setHint(CharSequence hint) {
        this.hint = hint;

        return this;
    }

    public Supplier<CharSequence> getHintSupplier() {
        return this.hintSupplier;
    }

    public BaseSettingItem<T> setHintSupplier(Supplier<CharSequence> s) {
        this.hintSupplier = s;

        return this;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public Supplier<Integer> getBadgeSupplier() {
        return badgeSupplier;
    }

    public void setBadgeSupplier(Supplier<Integer> badgeSupplier) {
        this.badgeSupplier = badgeSupplier;
    }

    public int getChevron() {
        return this.chevron;
    }

    public BaseSettingItem<T> setChevron(int visibility) {
        this.chevron = visibility;

        return this;
    }

    public Consumer<T> getConsumer() {
        return this.consumer;
    }

    public BaseSettingItem<T> setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;

        return this;
    }

    public void setUserObject(T object) {
        this.userObject = object;
    }

    public T getUserObject() {
        return userObject;
    }

    public boolean isDividerVisible() {
        return this.dividerVisible;
    }

    public BaseSettingItem<T> setDividerVisible(boolean value) {
        this.dividerVisible = value;

        return this;
    }

    public int getDividerType() {
        return this.dividerType;
    }

    public BaseSettingItem<T> setDividerType(int value) {
        this.dividerType = value;

        return this;
    }
}
