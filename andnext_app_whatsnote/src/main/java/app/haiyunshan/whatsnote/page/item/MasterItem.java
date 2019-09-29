package app.haiyunshan.whatsnote.page.item;

import android.net.Uri;
import app.haiyunshan.whatsnote.preference.entity.ProfileEntity;
import org.joda.time.LocalDateTime;

import java.util.function.Consumer;

public class MasterItem {

    Uri portraitUri;
    CharSequence name;
    CharSequence signature;

    LocalDateTime dateTime;

    Consumer<MasterItem> consumer;

    public MasterItem(ProfileEntity entity, Consumer<MasterItem> consumer) {
        this.consumer = consumer;
        this.dateTime = LocalDateTime.now();

        this.set(entity);
    }

    public void set(ProfileEntity entity) {
        this.portraitUri = entity.getPortraitUri();
        this.name = entity.getName();

        this.signature = entity.getSignature();
    }

    public Uri getPortraitUri() {
        return portraitUri;
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getSignature() {
        return signature;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Consumer<MasterItem> getConsumer() {
        return consumer;
    }
}
