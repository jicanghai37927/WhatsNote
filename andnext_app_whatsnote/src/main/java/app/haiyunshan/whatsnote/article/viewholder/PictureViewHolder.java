package app.haiyunshan.whatsnote.article.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import club.andnext.helper.SoftInputHelper;
import club.andnext.utils.AlertDialogUtils;
import club.andnext.utils.SoftInputUtils;
import club.andnext.widget.TargetSizeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;

/**
 *
 */
public class PictureViewHolder extends ComposeViewHolder<PictureEntity> {

    public static final int LAYOUT_RES_ID = R.layout.layout_compose_picture_list_item;

    static final String TAG = PictureViewHolder.class.getSimpleName();

    View pictureLayout;
    TargetSizeImageView pictureView;

    EditText editText;

    @Keep
    public PictureViewHolder(Callback callback, View itemView) {
        super(callback, itemView);
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    public void onViewCreated(@NonNull View view) {

        this.pictureLayout = view.findViewById(R.id.picture_layout);
        this.pictureView = view.findViewById(R.id.iv_picture);

        this.editText = view.findViewById(R.id.edit_text);

        {
            this.setTextChangeListener(new TextChangeListener(this, editText));
        }
    }

    @Override
    public void onBind(PictureEntity item, int position) {
        super.onBind(item, position);

        {
            editText.setEnabled(callback.isEnable());
            editText.setOnFocusChangeListener(this::onEditFocusChanged);
        }

        {
            // set break strategy to request layout
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editText.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
            }

            editText.setText(item.getText());
            editText.setVisibility(editText.length() > 0? View.VISIBLE: View.GONE);
        }

        {
            pictureLayout.setEnabled(callback.isEnable());
            pictureLayout.setOnClickListener(this::onItemClick);
            pictureLayout.setOnLongClickListener(this::onItemLongClick);
        }

        {
            int maxWidth = callback.getMaxWidth();
            int width = item.getWidth();
            int height = item.getHeight();
            if (width > maxWidth / 2) {
                width = maxWidth;
                height = width * item.getHeight() / item.getWidth();
            }

            pictureView.setTargetSize(width, height);
        }

        {
            RequestOptions options = createRequestOptions(item);

            Glide.with(itemView)
                    .load(item.getUri())
                    .apply(options)
                    .listener(new PictureListener())
                    .into(pictureView);
        }


    }

    @Override
    public void onSoftInputChanged(SoftInputHelper helper, boolean visible) {
        super.onSoftInputChanged(helper, visible);
        if (!visible && editText.hasFocus()) {
            editText.clearFocus();
        }
    }

    void onItemClick(View view) {
        if (editText.hasFocus()) {
            SoftInputUtils.hide(getContext(), editText);
        }

        this.requestView();
    }

    boolean onItemLongClick(View view) {

        MenuItemClickListener listener = new MenuItemClickListener();

        {
            listener.put(R.id.menu_delete, (id) -> this.requestRemove());
            listener.put(R.id.menu_name, (id) -> this.requestEdit());
        }

        {
            int menuRes = R.menu.menu_compose_picture;
            PopupMenu popupMenu = new PopupMenu(getContext(), itemView);
            popupMenu.inflate(menuRes);
            popupMenu.setOnMenuItemClickListener(listener);
            popupMenu.show();
        }

        return true;
    }

    void onEditFocusChanged(View v, boolean hasFocus) {
        if (!hasFocus) {
            editText.setVisibility(editText.length() > 0? View.VISIBLE: View.GONE);
            SoftInputUtils.hide(getContext(), editText);
        }
    }

    @Override
    void requestRemove() {

        // save document first
        {
            callback.requestSave(this);
        }

        {
            Context context = getContext();
            CharSequence title = "确定删除图片？";
            CharSequence msg = null;
            CharSequence negativeButton = context.getString(android.R.string.cancel);
            CharSequence positiveButton = context.getString(android.R.string.yes);
            DialogInterface.OnClickListener listener = ((dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    super.requestRemove();
                }
            });

            AlertDialogUtils.showConfirm(context, title, msg, negativeButton, positiveButton, listener);
        }
    }

    void requestEdit() {
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
        editText.setSelection(editText.length());
        editText.post(()-> SoftInputUtils.show(getContext(), editText));
    }

    @Override
    public void save() {
        entity.setText(editText.getText());
    }

    RequestOptions createRequestOptions(PictureEntity entity) {
        RequestOptions options = new RequestOptions();

        // picture size
        int width = entity.getWidth();
        int height = entity.getHeight();

        // sample it for 1, 2, 4, 8, ...
        {
            int maxWidth = callback.getMaxWidth();
            int maxHeight = callback.getMaxHeight();

            int widthSampleFactor = getSampleFactor(width, maxWidth);
            int heightSampleFactor = getSampleFactor(height, 6 * maxHeight);

            int sampleFactor = Math.max(widthSampleFactor, heightSampleFactor);
            sampleFactor = Math.max(1, Integer.highestOneBit(sampleFactor));

            if (sampleFactor > 1) {
                width /= sampleFactor;
                height /= sampleFactor;
            }
        }

        {
            options.dontTransform();
            options.override(width, height);
            options.downsample(DownsampleStrategy.FIT_CENTER);
            options.signature(new ObjectKey(entity.getSignature()));
        }

        return options;
    }

    static int getSampleFactor(int source, int target) {
        int scale = 1;

        while ((source / scale) > target) {
            scale *= 2;
        }

        return scale;
    }

    /**
     *
     */
    private static class PictureListener implements RequestListener<Drawable> {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

            Log.w(TAG, "onResourceReady: width = " + resource.getIntrinsicWidth() + ", height = " + resource.getIntrinsicHeight());

            return false;
        }
    }
}
