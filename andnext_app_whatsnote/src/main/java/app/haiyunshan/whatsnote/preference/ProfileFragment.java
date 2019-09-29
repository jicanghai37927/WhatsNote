package app.haiyunshan.whatsnote.preference;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.haiyunshan.whatsnote.ComposeTextActivity;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.preference.entity.PreferenceEntity;
import app.haiyunshan.whatsnote.preference.entity.ProfileEntity;
import app.haiyunshan.whatsnote.setting.BasePreferenceFragment;
import app.haiyunshan.whatsnote.setting.item.*;
import app.haiyunshan.whatsnote.setting.viewholder.*;
import club.andnext.dialog.PopupMenuDialogFragment;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.ucrop.UCrop;
import club.andnext.utils.UriUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BasePreferenceFragment {

    static final String ID_PROFILE  = "profile";
    static final String ID_VISION   = "vision";

    RequestResultManager requestResultManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestResultManager = new RequestResultManager();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.setTitle("笔名");
        }

        {
            dividerDecoration.setDrawTop(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected List<BaseSettingItem> createList() {
        List<BaseSettingItem> list = new ArrayList<>();

        {
            ProfileSettingItem item = new ProfileSettingItem(PreferenceEntity.obtain().getProfile());
            item.setId(ID_PROFILE);
            item.setDividerVisible(false);

            item.setConsumer((obj) -> requestPicture((file, sourceUri) -> {
                ProfileEntity entity = PreferenceEntity.obtain().getProfile();
                Uri targetUri = entity.nextPortraitUri();

                RequestCropDelegate delegate = new RequestCropDelegate(this,
                        file,
                        sourceUri,
                        targetUri,

                        (uCrop) -> {

                            uCrop.withAspectRatio(1, 1);

                            UCrop.Options options = new UCrop.Options();
                            options.setCircleDimmedLayer(true);
                            options.setHideBottomControls(true);
                            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                            options.setCompressionQuality(60);
                            options.setShowCropGrid(false);
                            options.setShowCropFrame(false);

                            uCrop.withOptions(options);
                        },

                        (uri) -> setPortrait(uri));

                requestResultManager.request(delegate);
            }));

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(true);

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem("昵称");
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);
            item.setChevron(View.VISIBLE);

            item.setConsumer((obj) -> {
                RequestNameDelegate delegate = new RequestNameDelegate(this, (name) -> this.setName(name));
                requestResultManager.request(delegate);
            });

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem("个性签名");
            item.setDividerType(BaseSettingItem.DIVIDER_ITEM);
            item.setChevron(View.VISIBLE);

            item.setConsumer((obj) -> {
                RequestSignatureDelegate delegate = new RequestSignatureDelegate(this, (signature) -> this.setSignature(signature));
                requestResultManager.request(delegate);
            });

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("");
            item.setDividerVisible(false);

            list.add(item);
        }

        {
            TitleSettingItem item = new TitleSettingItem("个性图签");

            list.add(item);
        }

        {
            BaseSettingItem item = new BaseSettingItem("选取新的图片签名");
            item.setDividerType(BaseSettingItem.DIVIDER_NAME);
            item.setChevron(View.INVISIBLE);

            item.setConsumer((obj) -> requestPicture((file, sourceUri) -> {
                ProfileEntity entity = PreferenceEntity.obtain().getProfile();
                Uri targetUri = entity.nextVisionUri();

                RequestCropDelegate delegate = new RequestCropDelegate(this,
                        file,
                        sourceUri,
                        targetUri,

                        (uCrop) -> {

                            uCrop.withAspectRatio(1.f * ProfileEntity.VISION_WIDTH / ProfileEntity.VISION_HEIGHT, 1);

                            UCrop.Options options = new UCrop.Options();
                            options.setCircleDimmedLayer(false);
                            options.setHideBottomControls(true);
                            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                            options.setCompressionQuality(60);
                            options.setShowCropGrid(false);
                            options.setShowCropFrame(false);

                            uCrop.withOptions(options);
                        },

                        (uri) -> setVision(uri));

                requestResultManager.request(delegate);
            }));

            list.add(item);
        }

        {
            PictureSettingItem item = new PictureSettingItem(
                    PreferenceEntity.obtain().getProfile().getVisionUri(),
                    ProfileEntity.VISION_WIDTH, ProfileEntity.VISION_HEIGHT);
            item.setId(ID_VISION);

            item.setErrorResId(R.drawable.ic_profile_vision);

            list.add(item);
        }

        {
            ExplainSettingItem item = new ExplainSettingItem("以图片方式分享笔记时，将显示图片签名。");
            item.setDividerVisible(false);

            list.add(item);
        }

        return list;
    }

    @Override
    protected void buildAdapter(BridgeAdapter adapter) {
        adapter.bind(TitleSettingItem.class,
                new BridgeBuilder(TitleSettingViewHolder.class, TitleSettingViewHolder.LAYOUT_RESOURCE_ID));

        adapter.bind(ExplainSettingItem.class,
                new BridgeBuilder(ExplainSettingViewHolder.class, ExplainSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(ProfileSettingItem.class,
                new BridgeBuilder(MasterSettingViewHolder.class, MasterSettingViewHolder.LAYOUT_RESOURCE_ID));

        adapter.bind(BaseSettingItem.class,
                new BridgeBuilder(BaseSettingViewHolder.class, BaseSettingViewHolder.LAYOUT_RES_ID));

        adapter.bind(PictureSettingItem.class,
                new BridgeBuilder(PictureSettingViewHolder.class, PictureSettingViewHolder.LAYOUT_RES_ID));
    }

    void setName(String text) {

        {
            ProfileEntity entity = PreferenceEntity.obtain().getProfile();
            entity.setName(text);

            PreferenceEntity.obtain().save();
        }

        {
            ProfileSettingItem item = getItem(ID_PROFILE);
            item.setName(text);

            this.notifyItemChanged(item);
        }
    }

    void setSignature(String text) {

        {
            ProfileEntity entity = PreferenceEntity.obtain().getProfile();
            entity.setSignature(text);

            PreferenceEntity.obtain().save();
        }

        {
            ProfileSettingItem item = getItem(ID_PROFILE);
            item.setHint(text);

            this.notifyItemChanged(item);
        }
    }

    void setPortrait(Uri uri) {

        {
            ProfileEntity entity = PreferenceEntity.obtain().getProfile();
            entity.setPortraitUri(uri);

            PreferenceEntity.obtain().save();
        }

        {
            ProfileSettingItem item = getItem(ID_PROFILE);
            item.setIconUri(uri);

            this.notifyItemChanged(item);
        }
    }

    void setVision(Uri uri) {

        {
            ProfileEntity entity = PreferenceEntity.obtain().getProfile();
            entity.setVisionUri(uri);

            PreferenceEntity.obtain().save();
        }

        {
            PictureSettingItem item = getItem(ID_VISION);
            item.setIconUri(uri);

            this.notifyItemChanged(item);
        }
    }

    void requestPicture(final BiConsumer<File, Uri> consumer) {

        MenuItemClickListener listener = new MenuItemClickListener();

        {
            listener.put(R.id.menu_camera, e -> {
                RequestCameraDelegate delegate = new RequestCameraDelegate(this, consumer);
                requestResultManager.request(delegate);
            });

            listener.put(R.id.menu_photo, e -> {
                RequestPhotoDelegate delegate = new RequestPhotoDelegate(this, consumer);
                requestResultManager.request(delegate);
            });
        }

        {
            PopupMenuDialogFragment dialogFragment = new PopupMenuDialogFragment();
            dialogFragment.setTheme(R.style.DialogDimTheme);
            dialogFragment.setMenuResource(R.menu.menu_profile);
            dialogFragment.setOnMenuItemClickListener(listener);

            dialogFragment.showNow(getFragmentManager(), "profile");
        }
    }

    /**
     *
     */
    private static class RequestNameDelegate extends BaseRequestDelegate {

        Consumer<String> consumer;

        public RequestNameDelegate(Fragment f, Consumer<String> consumer) {
            super(f);
            this.consumer = consumer;
        }

        @Override
        public boolean request() {
            String title = "昵称";
            String text = PreferenceEntity.obtain().getProfile().getName();

            ComposeTextActivity.Builder builder = new ComposeTextActivity.Builder(title, text);

            {
                ComposeTextActivity.Options options = new ComposeTextActivity.Options();
                options.setHint("佚名");

                options.setMaxLength(32);
                options.setMinLines(1);
                options.setMaxLines(1);

//                    options.setName("昵称");
                options.setExplain("天下谁人不识君。");

                builder.withOptions(options);
            }

            builder.start(parent, getRequestCode());

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                String text = data.getStringExtra(ComposeTextActivity.Builder.EXTRA_TEXT);
                if (!TextUtils.isEmpty(text)) {
                    consumer.accept(text);
                }

            }
        }
    }

    /**
     *
     */
    private static class RequestSignatureDelegate extends BaseRequestDelegate {

        Consumer<String> consumer;

        public RequestSignatureDelegate(Fragment f, Consumer<String> consumer) {
            super(f);

            this.consumer = consumer;
        }

        @Override
        public boolean request() {
            String title = "个性签名";
            String text = PreferenceEntity.obtain().getProfile().getSignature();

            ComposeTextActivity.Builder builder = new ComposeTextActivity.Builder(title, text);

            {
                ComposeTextActivity.Options options = new ComposeTextActivity.Options();
                options.setHint("个性签名");

                options.setMaxLength(256);
                options.setMinLines(4);
//                    options.setMaxLines(4);

//                    options.setName("昵称");
                options.setExplain("仰天大笑出门去，我辈岂是蓬蒿人。");

                builder.withOptions(options);
            }

            builder.start(parent, getRequestCode());

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                String text = data.getStringExtra(ComposeTextActivity.Builder.EXTRA_TEXT);
                if (!TextUtils.isEmpty(text)) {
                    consumer.accept(text);
                }
            }
        }
    }

    /**
     *
     */
    private static class RequestCameraDelegate extends BaseRequestDelegate {

        File imageFile;

        Uri imageUri;

        BiConsumer<File, Uri> consumer;

        public RequestCameraDelegate(Fragment f, BiConsumer<File, Uri> consumer) {
            super(f);

            this.consumer = consumer;

            {
                File file = f.getActivity().getExternalCacheDir();
                file.mkdirs();
                file = new File(file, System.currentTimeMillis() + ".jpg");

                this.imageFile = file;
                this.imageUri = UriUtils.fromFile(context, file);
            }
        }

        @Override
        public boolean request() {

            Intent intent = new Intent();
            {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }

            try {

                parent.startActivityForResult(intent, getRequestCode());

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            if (!imageFile.exists()) {
                return;
            }

            consumer.accept(imageFile, imageUri);
        }
    }

    /**
     *
     */
    private static class RequestPhotoDelegate extends BaseRequestDelegate {

        BiConsumer<File, Uri> consumer;

        public RequestPhotoDelegate(Fragment f, BiConsumer<File, Uri> consumer) {
            super(f);

            this.consumer = consumer;
        }

        @Override
        public boolean request() {

            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

            try {
                parent.startActivityForResult(intent, getRequestCode());

                return true;
            } catch (Exception e) {

            }

            return false;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {

            if ((resultCode != RESULT_OK) || (data == null)) {
                return;
            }

            ArrayList<Uri> list = new ArrayList<>();

            {
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        list.add(uri);
                    }
                }

                if (list.isEmpty()) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        list.add(uri);
                    }
                }
            }

            {
                consumer.accept(null, list.get(0));
            }

        }
    }

    /**
     *
     */
    private class RequestCropDelegate extends BaseRequestDelegate {

        File sourceFile;

        Uri sourceUri;
        Uri targetUri;

        Consumer<UCrop> cropConsumer;
        Consumer<Uri> uriConsumer;

        public RequestCropDelegate(Fragment f, File file, Uri sourceUri, Uri targetUri, Consumer<UCrop> cropConsumer, Consumer<Uri> uriConsumer) {
            super(f);

            this.sourceFile = file;

            this.sourceUri = sourceUri;
            this.targetUri = targetUri;

            this.cropConsumer = cropConsumer;
            this.uriConsumer = uriConsumer;
        }

        @Override
        public boolean request() {
            UCrop uCrop = UCrop.of(sourceUri, targetUri);
            cropConsumer.accept(uCrop);

            uCrop.start(parent.getActivity(), parent, getRequestCode());
            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (sourceFile != null) {
                sourceFile.delete();
            }

            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            File file = new File(targetUri.getPath());
            if (!file.exists()) {
                return;
            }

            uriConsumer.accept(targetUri);
        }
    }
}
