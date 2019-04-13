package com.example.lishanxin.commonuse.project;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.redirect.wangxs.qiantu.R;
import com.redirect.wangxs.qiantu.application.AppContext;
import com.redirect.wangxs.qiantu.common.app.PresenterActivity;
import com.redirect.wangxs.qiantu.common.recycler.RecyclerAdapter;
import com.redirect.wangxs.qiantu.factory.api.result.personalcenter.CollectionModel;
import com.redirect.wangxs.qiantu.factory.presenter.record.MyCollectionContract;
import com.redirect.wangxs.qiantu.factory.presenter.record.MyCollectionPresenter;
import com.redirect.wangxs.qiantu.utils.GlideApp;
import com.redirect.wangxs.qiantu.views.RoundLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class MyCollectActivity extends PresenterActivity<MyCollectionContract.Presenter> implements MyCollectionContract.View {


    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.rf)
    SmartRefreshLayout rf;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.layout_recommend)
    ConstraintLayout layoutRecommend;
    @BindView(R.id.tb_leftButton)
    Button tbLeftButton;
    @BindView(R.id.tb_titleText)
    TextView tbTitleText;

    // 当前的Adapter，实时切换为adapter或recommendAdapter
    private RecyclerAdapter<CollectionModel.Entity> mAdapter;
    // 有收藏时的Adapter
    private Adapter adapter = new Adapter();
    // 没有收藏时，推荐界面的Adapter
    private RecommendAdapter recommendAdapter = new RecommendAdapter();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_my_collect;
    }

    @Override
    protected MyCollectionContract.Presenter initPresenter() {
        return new MyCollectionPresenter(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mAdapter = adapter;
        lvData.setAdapter(mAdapter);
        lvData.addOnScrollListener(new AutoLoadScrollListener());
        showMyCollection();
        lvData.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Adapter.CollectViewHolder viewHolder = (Adapter.CollectViewHolder) view.getTag(R.id.tag_recycler_holder);
                int position = viewHolder.getAdapterPosition();
                ImageView view1 = viewHolder.mainImage;
                ImageView view2 = viewHolder.imageHead;
                CollectionModel.Entity entity = getRecyclerAdapter().getItems().get(position);
                GlideApp.with(view.getContext()).load(entity.getCover_image()).diskCacheStrategy(DiskCacheStrategy.ALL).into(view1);
                GlideApp.with(view.getContext()).load(entity.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(view2);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Adapter.CollectViewHolder viewHolder = (Adapter.CollectViewHolder) view.getTag(R.id.tag_recycler_holder);
                ImageView view1 = viewHolder.mainImage;
                GlideApp.with(view.getContext()).clear(view1);
                GlideApp.with(view.getContext()).clear(viewHolder.imageHead);
                releaseImageViewResouce(view1);
            }
        });
        tbTitleText.setText(R.string.my_collect);
    }

    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    private class AutoLoadScrollListener extends RecyclerView.OnScrollListener {
        public AutoLoadScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState){
                case SCROLL_STATE_IDLE://屏幕不再滚动
                    if (recyclerView.getContext() != null) Glide.with(recyclerView.getContext()).resumeRequests();
                    break;
//                case SCROLL_STATE_DRAGGING://屏幕还在滚动，且手指还在屏幕上
//                    if (recyclerView.getContext() != null) Glide.with(recyclerView.getContext()).pauseRequests();
//                    break;
                case SCROLL_STATE_SETTLING://屏幕处于惯性滚动当中
                    if (recyclerView.getContext() != null) Glide.with(recyclerView.getContext()).pauseRequests();
                    break;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

    }
    @Override
    public RecyclerAdapter<CollectionModel.Entity> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    protected void initData() {
        super.initData();

        mPresenter.start();
    }

    @Override
    public void onAdapterDataChanged() {

    }

    @Override
    public void showMyCollection() {
        layoutRecommend.setVisibility(View.GONE);
        mAdapter = adapter;
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.swapAdapter(adapter, true);
    }

    @Override
    public void showRecommendCollection() {
        layoutRecommend.setVisibility(View.VISIBLE);
        mAdapter = recommendAdapter;
        lvData.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        lvData.swapAdapter(recommendAdapter, true);
    }

    @OnClick(R.id.tb_leftButton)
    public void onViewClicked() {
        finish();
    }


    public static class Adapter extends RecyclerAdapter<CollectionModel.Entity> {


        public Adapter() {
        }

        public Adapter(AdapterListener<CollectionModel.Entity> listener) {
            super(listener);
        }

        public Adapter(List<CollectionModel.Entity> cameraRecordModels, AdapterListener<CollectionModel.Entity> listener) {
            super(cameraRecordModels, listener);
        }

        @Override
        protected int getItemViewType(int position, CollectionModel.Entity collectionModel) {
            return R.layout.cell_my_collection;
        }



        @Override
        protected ViewHolder<CollectionModel.Entity> onCreateViewHolder(View root, int viewType) {
            ViewStub stub = root.findViewById(R.id.view_stub_header);
            stub.setLayoutResource(R.layout.image_travel_record);
            stub.inflate();
            return new CollectViewHolder(root);
        }

        static class CollectViewHolder extends ViewHolder<CollectionModel.Entity> {
            @BindView(R.id.image_head)
            CircleImageView imageHead;
            @BindView(R.id.name)
            TextView name;
            @BindView(R.id.time)
            TextView time;
            @BindView(R.id.position_name)
            TextView positionName;
            @BindView(R.id.title)
            TextView title;
            @BindView(R.id.main_image)
            ImageView mainImage;
            @BindView(R.id.item_iv_like)
            ImageView itemIvLike;
            @BindView(R.id.item_tv_likenum)
            TextView itemTvLikenum;
            @BindView(R.id.item_ll_like)
            LinearLayout itemLlLike;
            @BindView(R.id.item_iv_talk)
            ImageView itemIvTalk;
            @BindView(R.id.item_tv_talknum)
            TextView itemTvTalknum;
            @BindView(R.id.item_ll_talk)
            LinearLayout itemLlTalk;
            @BindView(R.id.iv_share)
            ImageView ivShare;
            @BindView(R.id.tv_share_num)
            TextView tvShareNum;
            @BindView(R.id.ll_share)
            LinearLayout llShare;
            @BindView(R.id.item_container)
            LinearLayout itemContainer;
            @BindView(R.id.roundLayout)
            RoundLayout roundLayout;

            public CollectViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(CollectionModel.Entity model) {
                name.setText(model.getNickname());
//                AppContext.getInstance().setImageViewPath(model.getAvatar(), imageHead);
                time.setText(model.getCreatetime());
                positionName.setText(model.getTermini());
                title.setText(model.getTitle());
//                AppContext.getInstance().setImageViewPath(model.getCover_image(), mainImage);
                itemTvLikenum.setText(model.getPraise_num() + "");
                itemTvTalknum.setText(model.getComm_num() + "");
                tvShareNum.setText(model.getShare_num() + "");
            }
        }
    }

    public static class RecommendAdapter extends RecyclerAdapter<CollectionModel.Entity> {


        public RecommendAdapter() {
        }

        public RecommendAdapter(AdapterListener<CollectionModel.Entity> listener) {
            super(listener);
        }

        public RecommendAdapter(List<CollectionModel.Entity> cameraRecordModels, AdapterListener<CollectionModel.Entity> listener) {
            super(cameraRecordModels, listener);
        }

        @Override
        protected int getItemViewType(int position, CollectionModel.Entity collectionModel) {
            return R.layout.cell_collect_recommend;
        }

        @Override
        protected ViewHolder<CollectionModel.Entity> onCreateViewHolder(View root, int viewType) {
            if (root.getParent() != null){
                ((FrameLayout)root.getParent()).setBackgroundColor(root.getContext().getResources().getColor(R.color.touming));
            }
            return new RecommendViewHolder(root);
        }

        static class RecommendViewHolder extends ViewHolder<CollectionModel.Entity> {
            @BindView(R.id.recommend_main_image)
            ImageView recommendMainImage;
            @BindView(R.id.position_name)
            TextView positionName;
            @BindView(R.id.textView6)
            TextView textView6;
            @BindView(R.id.image_head)
            CircleImageView imageHead;
            @BindView(R.id.name)
            TextView name;
            @BindView(R.id.time)
            TextView time;
            @BindView(R.id.recommend_heart)
            ImageView recommendHeart;
            @BindView(R.id.tv_focus_num)
            TextView tvFocusNum;

            public RecommendViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            protected void onBind(CollectionModel.Entity entity) {
                AppContext.getInstance().setImageViewPath(entity.getCover_image(), recommendMainImage);
                positionName.setText(entity.getTermini());
                textView6.setText(entity.getTitle());
                AppContext.getInstance().setImageViewPath(entity.getAvatar(), imageHead);
                name.setText(entity.getNickname());
                time.setText(entity.getCreatetime());
                tvFocusNum.setText(entity.getPraise_num() + "");
            }
        }
    }

    public static class MyDecoration extends RecyclerView.ItemDecoration {

        int space;

        public MyDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = space;
            outRect.left = space;
            outRect.right = space;
            outRect.top = space;
        }
    }


}
