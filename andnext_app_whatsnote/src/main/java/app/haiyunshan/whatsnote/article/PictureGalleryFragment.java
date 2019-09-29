package app.haiyunshan.whatsnote.article;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import app.haiyunshan.whatsnote.PictureListActivity;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.MenuItemClickListener;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.record.entity.ImageEntity;
import app.haiyunshan.whatsnote.widget.SearchTitleBar;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.widget.ClipPageTransformer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import static android.app.Activity.RESULT_OK;
import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ORIENTATION_USE_EXIF;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureGalleryFragment extends BasePictureFragment {

    private static final float MAX_SCALE = 5.f;

    SearchTitleBar titleBar;
    View bottomBar;

    ViewPager viewPager;
    GalleryAdapter adapter;

    boolean actionVisible = true;
    RequestResultManager requestResultManager;

    public PictureGalleryFragment() {
        this.requestResultManager = new RequestResultManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            int flags = View.SYSTEM_UI_FLAG_VISIBLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            view.setSystemUiVisibility(flags);
        }

        {
            TransitionDrawable d = (TransitionDrawable)(view.getBackground());
            d.setCrossFadeEnabled(true);
        }

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            TextView backBtn = titleBar.getBackButton();
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setText("完成");
            backBtn.setOnClickListener(this::onBackClick);

            MenuItemClickListener listener = new MenuItemClickListener();
            listener.put(R.id.menu_gallery_list, (id) ->
                requestResultManager.request(new RequestListDelegate(this))
            );

            titleBar.getToolbar().inflateMenu(R.menu.menu_picture_gallery);
            titleBar.getToolbar().setOnMenuItemClickListener(listener);
        }

        {
            this.bottomBar = view.findViewById(R.id.bottom_bar);

        }

        {
            this.viewPager = view.findViewById(R.id.view_pager);
            int gutterWidth = getResources().getDimensionPixelSize(R.dimen.pictureGalleryGutter);
            viewPager.setPageTransformer(false, new ClipPageTransformer(gutterWidth));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Window window = getActivity().getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            
        }

        {
            getView().setOnApplyWindowInsetsListener((v, insets) -> {
                titleBar.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                bottomBar.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());

                return insets.consumeSystemWindowInsets();
            });
        }

        {
            this.adapter = new GalleryAdapter();
        }

        {
            this.viewPager.setAdapter(adapter);

            int position = provider.indexOf(pictureId);
            if (position > 0) {
                viewPager.setCurrentItem(position, false);
            }

            this.updateTitle(position);

            viewPager.addOnPageChangeListener(new PageChangeListener());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        this.setActionVisible(true);
    }

    void onBackClick(View view) {
        getActivity().onBackPressed();
    }

    void updateTitle(int position) {
        position = (position < 0)? 0: position;

        CharSequence title;
        if (provider.size() > 1) {
            title = (position + 1) + "/" + provider.size();
        } else {
            ImageEntity entity = ImageEntity.obtain(provider.get(position).getId(), documentId);
            title = entity.getName();
        }

        titleBar.setTitle(title);
    }

    void toggleAction() {
        this.setActionVisible(!isActionVisible());
    }

    void setActionVisible(boolean value) {

        if (!(isActionVisible() ^ value)) {
            return;
        }

        this.actionVisible = value;

        {
            if (value) {
                titleBar.animate().translationY(0);
                bottomBar.animate().translationY(0);
            } else {
                titleBar.animate().translationY(-titleBar.getHeight());
                bottomBar.animate().translationY(bottomBar.getHeight());
            }
        }

        {
            TransitionDrawable d = (TransitionDrawable) (getView().getBackground());

            if (value) {
                d.reverseTransition(getResources().getInteger(android.R.integer.config_shortAnimTime));
            } else {
                d.startTransition(getResources().getInteger(android.R.integer.config_shortAnimTime));
            }
        }

        {
            View view = this.getView();
            int flags = view.getSystemUiVisibility();

            if (value) {
                flags &= (~View.SYSTEM_UI_FLAG_FULLSCREEN);
                flags &= (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } else {
                flags |= (View.SYSTEM_UI_FLAG_FULLSCREEN);
                flags |= (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }

            view.setSystemUiVisibility(flags);
        }

    }

    boolean isActionVisible() {
        return this.actionVisible;
    }

    static RequestOptions createRequestOptions(Context context, PictureEntity entity) {
        RequestOptions options = new RequestOptions();

        // picture size
        int width = entity.getWidth();
        int height = entity.getHeight();

        // sample it for 1, 2, 4, 8, ...
        {
            DisplayMetrics m = context.getResources().getDisplayMetrics();

            int maxWidth = m.widthPixels;
            int maxHeight = m.heightPixels;

            int widthSampleFactor = getSampleFactor(width, maxWidth);
            int heightSampleFactor = getSampleFactor(height, maxHeight);

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

    static float getDoubleTapScale(int width, int height, int viewWidth, int viewHeight) {
        if (viewWidth <= 0 || viewHeight <= 0) {
            return 1;
        }

        float sx = viewWidth * 1.f / width;
        float sy = viewHeight * 1.f / height;

        float scale = Math.max(sx, sy);
        if (Math.abs(sx - sy) < Float.MIN_NORMAL) {
            scale = 2 * sx;
        }

        return scale;
    }

    /**
     *
     */
    private class GalleryViewHolder extends BridgeViewHolder<PictureEntity> {

        public static final int LAYOUT_RES_ID = R.layout.layout_picture_gallery_item;

        SubsamplingScaleImageView pictureView;

        @Keep
        public GalleryViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.pictureView = (SubsamplingScaleImageView)view;

            pictureView.setOrientation(ORIENTATION_USE_EXIF);
            pictureView.setMaxScale(MAX_SCALE);
            pictureView.setOnStateChangedListener(new PictureStateChangedListener());
        }

        @Override
        public void onBind(PictureEntity item, int position) {

            Glide.with(pictureView)
                    .asBitmap()
                    .apply(createRequestOptions(getContext(), item))
                    .load(item.getUri())
                    .into(new CustomViewTarget<SubsamplingScaleImageView, Bitmap>(pictureView) {

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            ImageSource imageSource = ImageSource.uri(item.getUri());
                            ImageSource previewSource = null;
                            ImageViewState state = null;

                            view.setImage(imageSource, previewSource, state);
                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ImageSource imageSource = ImageSource.uri(item.getUri());
                            imageSource.dimensions(item.getWidth(), item.getHeight());

                            ImageSource previewSource = ImageSource.cachedBitmap(resource);

                            ImageViewState state = null;

                            view.setImage(imageSource, previewSource, state);
                        }

                        @Override
                        protected void onResourceCleared(@Nullable Drawable placeholder) {

                        }
                    });

            pictureView.post(() -> {
                int width = item.getWidth();
                int height = item.getHeight();
                int viewWidth = pictureView.getWidth();
                int viewHeight = pictureView.getHeight();

                float scale = getDoubleTapScale(width, height, viewWidth, viewHeight);
                float max = pictureView.getMaxScale();
                scale = (scale > max)? max: scale;

                pictureView.setDoubleTapZoomScale(scale);
            });
        }

        void onItemClick(View view) {
            toggleAction();
        }
    }

    /**
     *
     */
    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            {
                updateTitle(position);
            }

            {
                String id = provider.get(position).getId();

                Intent intent = new Intent();
                intent.putExtra("id", id);

                getActivity().setResult(Activity.RESULT_OK, intent);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     *
     */
    private class RequestListDelegate extends BaseRequestDelegate {

        public RequestListDelegate(Fragment f) {
            super(f);
        }

        @Override
        public boolean request() {
            String pictureId = provider.get(viewPager.getCurrentItem()).getId();
            PictureListActivity.startForResult(parent, documentId, pictureId, requestCode);

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {

            if ((resultCode != RESULT_OK) || (data == null)) {
                return;
            }

            String id = data.getStringExtra("id");
            if (TextUtils.isEmpty(id)) {
                return;
            }

            int index = provider.indexOf(id);
            if (index != viewPager.getCurrentItem()) {
                viewPager.setCurrentItem(index, false);
            }

        }
    }

    /**
     *
     */
    private class GalleryAdapter extends PagerAdapter {

        GalleryAdapter() {

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());

            View itemView = inflater.inflate(GalleryViewHolder.LAYOUT_RES_ID, container, false);
            container.addView(itemView);

            GalleryViewHolder holder = new GalleryViewHolder(itemView);
            holder.onViewCreated(itemView);
            holder.onBind(provider.get(position), position);

            return holder;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            GalleryViewHolder holder = (GalleryViewHolder)object;

            container.removeView(holder.itemView);
        }

        @Override
        public int getCount() {
            return provider.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            GalleryViewHolder holder = (GalleryViewHolder)object;

            return (holder.itemView == view);
        }
    }

    /**
     *
     */
    private class PictureStateChangedListener implements SubsamplingScaleImageView.OnStateChangedListener {

        @Override
        public void onScaleChanged(float newScale, int origin) {
            setActionVisible(false);
        }

        @Override
        public void onCenterChanged(PointF newCenter, int origin) {
            setActionVisible(false);
        }
    }

}
