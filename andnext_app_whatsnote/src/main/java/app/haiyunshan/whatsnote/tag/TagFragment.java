package app.haiyunshan.whatsnote.tag;


import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.haiyunshan.whatsnote.ComposeTagActivity;
import app.haiyunshan.whatsnote.R;
import app.haiyunshan.whatsnote.WhatsApp;
import app.haiyunshan.whatsnote.base.BaseRequestDelegate;
import app.haiyunshan.whatsnote.base.RequestResultManager;
import app.haiyunshan.whatsnote.record.entity.RecordEntity;
import app.haiyunshan.whatsnote.record.entity.TagEntity;
import app.haiyunshan.whatsnote.tag.entity.ColorEntity;
import app.haiyunshan.whatsnote.widget.TitleBar;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.widget.CircleColorView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagFragment extends Fragment {

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    TagProvider provider;

    CreateTagHeader header;
    List<TagEntity> data;

    RecordEntity recordEntity;

    RequestResultManager requestResultManager;

    public TagFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        {
            this.requestResultManager = new RequestResultManager();
        }

        {
            this.header = new CreateTagHeader(this::onCreateTag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            TitleBar titleBar = view.findViewById(R.id.title_bar);
            titleBar.setTitle("标签");
            titleBar.setNegativeVisible(false);
            titleBar.setPositiveButton("完成", this::onDoneClick);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);

            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            Bundle args = this.getArguments();
            String id = args.getString("id");
            this.recordEntity = RecordEntity.create(id).get();
        }

        {
            this.data = new ArrayList<>(TagEntity.obtain().getList());
        }

        {
            this.provider = new TagProvider();
            this.adapter = new BridgeAdapter(getActivity(), provider);

            adapter.bind(CreateTagHeader.class,
                    new BridgeBuilder(CreateTagViewHolder.class, CreateTagViewHolder.LAYOUT_RES_ID));

            adapter.bind(TagEntity.class,
                    new BridgeBuilder(TagViewHolder.class, TagViewHolder.LAYOUT_RES_ID, this));
        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        requestResultManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (recordEntity != null) {
            recordEntity.save();
        }

    }

    void onDoneClick(View view) {
        getActivity().onBackPressed();
    }

    void onCreateTag(CreateTagHeader header) {
        RequestCreateDelegate delegate = new RequestCreateDelegate(this);
        requestResultManager.request(delegate);
    }

    void requestTag(TagEntity entity) {
        if (recordEntity == null) {
            return;
        }

        List<String> list = recordEntity.getTagList();
        int index = list.indexOf(entity.getId());
        if (index < 0) {
            list.add(entity.getId());
        } else {
            list.remove(index);
        }

        index = data.indexOf(entity);
        adapter.notifyItemChanged(provider.getHeaderCount() + index);
    }

    /**
     *
     */
    private class TagProvider implements BridgeAdapterProvider {

        @Override
        public Object get(int position) {
            Object obj = null;

            if (header != null) {
                if (position == 0) {
                    obj = header;
                } else {
                    position -= 1;
                }
            }

            if (obj == null) {
                obj = data.get(position);
            }

            return obj;
        }

        @Override
        public int size() {
            int size = 0;
            size += (header == null)? 0: 1;
            size += (data == null)? 0: data.size();

            return size;
        }

        int getHeaderCount() {
            return (header == null)? 0: 1;
        }
    }

    /**
     *
     */
    private class RequestCreateDelegate extends BaseRequestDelegate {

        public RequestCreateDelegate(Fragment f) {
            super(f);
        }

        @Override
        public boolean request() {
            ComposeTagActivity.startForResult(parent, "", getRequestCode());

            return true;
        }

        @Override
        public void accept(Integer resultCode, Intent data) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                return;
            }

            String id = data.getStringExtra("id");
            if (TextUtils.isEmpty(id)) {
                return;
            }

            TagEntity.obtain().get(id).ifPresent(entity -> onResult(entity));
        }

        void onResult(TagEntity entity) {

            {
                recordEntity.getTagList().add(entity.getId());
                recordEntity.save();
            }

            {
                data.add(0, entity);
                adapter.notifyItemInserted(provider.getHeaderCount());
            }

        }
    }

    /**
     *
     */
    private static class CreateTagHeader {

        final Consumer<CreateTagHeader> consumer;

        CreateTagHeader(Consumer<CreateTagHeader> consumer) {
            this.consumer = consumer;
        }
    }

    /**
     *
     */
    private static class CreateTagViewHolder extends BridgeViewHolder<CreateTagHeader> {

        public static final int LAYOUT_RES_ID = R.layout.layout_create_tag_list_item;

        @Keep
        public CreateTagViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setClipToOutline(true);
            view.setOnClickListener(this::onItemClick);
        }

        @Override
        public void onBind(CreateTagHeader item, int position) {

        }

        void onItemClick(View view) {
            CreateTagHeader item = this.getItem();
            if (item != null && item.consumer != null) {
                item.consumer.accept(item);
            }
        }
    }

    /**
     *
     */
    private static class TagViewHolder extends BridgeViewHolder<TagEntity> {

        public static final int LAYOUT_RES_ID = R.layout.layout_tag_list_item;

        static final int BG_COLOR = 0xfff5f5f5;

        TagFragment parent;

        CircleColorView iconView;
        TextView nameView;
        ImageView checkView;

        @Keep
        public TagViewHolder(TagFragment f, View itemView) {
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
            view.setClipToOutline(true);

            this.iconView = view.findViewById(R.id.iv_icon);
            this.nameView = view.findViewById(R.id.tv_name);
            this.checkView = view.findViewById(R.id.iv_check);
        }

        @Override
        public void onBind(TagEntity item, int position) {
            ColorEntity color = item.getColor();

            {
                iconView.setColor(color.getIcon());
                nameView.setText(item.getName());
            }

            RecordEntity recordEntity = parent.recordEntity;
            if (recordEntity != null) {
                boolean isCheck = (recordEntity.getTagList().indexOf(item.getId()) >= 0);

                int textColor = isCheck? color.getColor(): 0xff5c5c5c;
                nameView.setTextColor(textColor);

                int tint = isCheck? color.getBackground(): BG_COLOR;
                itemView.getBackground().setTint(tint);

                checkView.setVisibility(isCheck? View.VISIBLE: View.INVISIBLE);
                if (isCheck) {
                    checkView.setImageTintList(ColorStateList.valueOf(color.getCheck()));
                }

            }
        }

        void onItemClick(View view) {
            parent.requestTag(getItem());
        }
    }
}
