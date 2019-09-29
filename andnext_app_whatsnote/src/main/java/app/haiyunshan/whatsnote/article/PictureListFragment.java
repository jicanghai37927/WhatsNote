package app.haiyunshan.whatsnote.article;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.article.entity.PictureEntity;
import app.haiyunshan.whatsnote.record.entity.ImageEntity;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.glide.RoundedMask;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.decoration.PlaceholderDecoration;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureListFragment extends BasePictureFragment {

    TitleBar titleBar;

    RecyclerView recyclerView;
    BridgeAdapter adapter;

    HashMap<String, PictureInfo> infoMap;
    HashMap<String, String> mimeMap;

    public PictureListFragment() {
        this.infoMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.titleBar = view.findViewById(R.id.title_bar);
            titleBar.setNegativeVisible(false);
            titleBar.setPositiveButton("完成", v -> getActivity().onBackPressed());
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            MarginDividerDecoration dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setDrawTop(false);
            dividerDecoration.setLastMargin(true);
            dividerDecoration.setMargin(getResources().getDimensionPixelSize(R.dimen.record_item_padding));
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
            PlaceholderDecoration decoration = new PlaceholderDecoration(getActivity());
            decoration.setMargin(getResources().getDimensionPixelSize(R.dimen.record_item_padding));
            recyclerView.addItemDecoration(decoration, 0);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            ImageEntity entity = ImageEntity.obtain(pictureId, documentId);
            titleBar.setTitle(entity.getName());
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), provider);

            adapter.bind(PictureEntity.class,
                    new BridgeBuilder(PictureViewHolder.class, PictureViewHolder.LAYOUT_RES_ID, this));
        }

        {
            recyclerView.setAdapter(adapter);
        }

        {
            final int index = provider.indexOf(pictureId);
            if (index >= 0) {
                recyclerView.post(() -> {
                    recyclerView.scrollToPosition(index);
                    LinearLayoutManager layout = (LinearLayoutManager)(recyclerView.getLayoutManager());
                    layout.scrollToPositionWithOffset(index, 0);
                });
            }
        }
    }

    void requestResult(PictureEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("id", entity.getId());

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
    }

    PictureInfo getInfo(PictureEntity entity) {
        return infoMap.computeIfAbsent(entity.getId(), (key) ->
            new PictureInfo(getActivity(), entity)
        );
    }

    String formatMimeType(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return "";
        }

        if (mimeMap == null) {
            mimeMap = new HashMap<>();

            mimeMap.put("image/png", "PNG 图像");
            mimeMap.put("image/jpeg", "JPEG 图像");
            mimeMap.put("image/gif", "GIF 图像");
            mimeMap.put("image/bmp", "BMP 图像");
        }

        return mimeMap.getOrDefault(mimeType.toLowerCase(), "");
    }

    static CharSequence formatSize(long size) {
        StringBuilder sb = new StringBuilder();

        {
            long c = 1024;
            long b = 1024 * c;
            long a = 1024 * b;
            if (size > a) {
                sb.append(String.format("%.1f", size * 1.f / a));
                sb.append(" GB");
            } else if (size > b) {
                sb.append(String.format("%.1f", size * 1.f / b));
                sb.append(" MB");
            } else if (size > c) {
                sb.append(String.format("%.1f", size * 1.f / c));
                sb.append(" KB");
            } else {
                sb.append(size);
                sb.append(" bytes");
            }
        }

        return sb;
    }

    /**
     *
     */
    private class PictureInfo {

        CharSequence info;

        PictureEntity entity;

        Context context;

        PictureInfo(Context context, PictureEntity entity) {
            this.context = context;
            this.entity = entity;
        }

        CharSequence getName() {
            return ImageEntity.obtain(entity.getId(), documentId).getName();
        }

        CharSequence getDimension() {
            String str = String.format("%1$d x %2$d", entity.getWidth(), entity.getHeight());
            return str;
        }

        CharSequence getInfo() {
            if (info != null) {
                return info;
            }

            StringBuilder sb = new StringBuilder();

            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(entity.getFile().getAbsolutePath(), options);

                String mime = options.outMimeType;
                sb.append(formatMimeType(mime));
            }

            if (sb.length() > 0) {
                sb.append(" - ");
            }

            {
                sb.append(formatSize(entity.getFile().length()));
            }

            this.info = sb;
            return info;
        }


    }

    /**
     *
     */
    private static class PictureViewHolder extends BridgeViewHolder<PictureEntity> {

        public static final int LAYOUT_RES_ID = R.layout.layout_picture_list_item;

        ImageView iconView;
        TextView nameView;
        TextView dimenView;
        TextView infoView;

        PictureListFragment parent;

        @Keep
        public PictureViewHolder(PictureListFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this::onItemClick);

            this.iconView = view.findViewById(R.id.iv_icon);
            this.nameView = view.findViewById(R.id.tv_name);
            this.dimenView = view.findViewById(R.id.tv_dimension);
            this.infoView = view.findViewById(R.id.tv_info);
        }

        @Override
        public void onBind(PictureEntity item, int position) {
            PictureInfo info = parent.getInfo(item);
            nameView.setText(info.getName());
            dimenView.setHint(info.getDimension());
            infoView.setHint(info.getInfo());

            final GradientDrawable d = (GradientDrawable)(getContext().getDrawable(R.drawable.shape_icon_mask));
            Glide.with(iconView)
                    .load(item.getUri())
                    .apply(RequestOptions.bitmapTransform(new RoundedMask(d)).signature(new ObjectKey(item.getSignature())))
                    .into(iconView);
        }

        void onItemClick(View view) {
            parent.requestResult(getItem());
        }
    }
}
