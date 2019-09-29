package app.haiyunshan.whatsnote.page.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.page.item.MasterItem;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.joda.time.LocalDateTime;

public class MasterViewHolder extends BridgeViewHolder<MasterItem> {

    public static final int LAYOUT_RES_ID = R.layout.layout_master_list_item;

    ImageView portraitView;
    TextView nameView;
    TextView dateView;

    TextView signatureView;

    @Keep
    public MasterViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {

        this.portraitView = view.findViewById(R.id.iv_portrait);
        portraitView.setClipToOutline(true);
        portraitView.setOnClickListener(this::onPortraitClick);

        this.nameView = view.findViewById(R.id.tv_name);

        this.dateView = view.findViewById(R.id.tv_date);

        this.signatureView = view.findViewById(R.id.tv_signature);
    }

    @Override
    public void onBind(MasterItem item, int position) {

        {

            if (item.getPortraitUri() != null) {
                Glide.with(portraitView)
                        .load(item.getPortraitUri())
                        .apply(RequestOptions.errorOf(R.drawable.ic_portrait).placeholder(R.drawable.ic_portrait).circleCrop())
                        .into(portraitView);
            } else {
                portraitView.setImageResource(R.drawable.ic_portrait);

            }
        }

        {
            nameView.setText(getName(item.getName()));
        }

        {
            dateView.setText(getDate(item.getDateTime()));
        }

        {
            signatureView.setText(getText(item.getSignature()));
        }

    }

    void onPortraitClick(View view) {
        MasterItem item = getItem();
        item.getConsumer().accept(item);
    }

    CharSequence getDate(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();

        return String.format("%1$d.%2$d.%3$d", year, month, day);
    }

    CharSequence getName(CharSequence input) {
        CharSequence name = input;
        name = (TextUtils.isEmpty(name))? "佚名" : name;

        return name;
    }

    CharSequence getText(CharSequence input) {
        CharSequence text = input;
        if (TextUtils.isEmpty(text)) {
            text = "个性签名";
        }

        return text;
    }
}
