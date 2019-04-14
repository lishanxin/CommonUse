package com.example.lishanxin.commonuse.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lishanxin.commonuse.R;

import java.util.List;

public abstract class AutoLoadRecyclerView extends RecyclerView {

    private onLoadMoreListener mListener;
    private boolean isLoadMore;
    public AutoLoadRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AutoLoadRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLoadRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {

        addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
                int position = viewHolder.getAdapterPosition();
                //获取position位置的数据，并传入
                List<Integer> ids = getImageViewIds();
                if (ids == null) return;
                for (Integer id : ids) {
                    View item = view.findViewById(id);
                    if (item instanceof ImageView){
                        reloadImageResouce((ImageView) item);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
                int position = viewHolder.getAdapterPosition();
                //获取position位置的数据，并传入
                List<Integer> ids = getImageViewIds();
                if (ids == null) return;
                for (Integer id : ids) {
                    View item = view.findViewById(id);
                    if (item instanceof ImageView){
                        releaseImageViewResouce((ImageView) item);
                    }
                }
            }
        });
    }

    protected void reloadImageResouce(ImageView item){
        Glide.with(item.getContext()).load(item.getTag(R.id.tag_image_url)).into(item);
        Glide.with(item.getContext()).clear(item);
    };

    protected abstract List<Integer> getImageViewIds();

    public void setOnPauseListenerParams(){

    }




    public interface onLoadMoreListener{
        void loadMore();
    }


    private class AutoLoadScrollListener extends OnScrollListener{
        public AutoLoadScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState){
                case SCROLL_STATE_IDLE://屏幕不再滚动
                    if (getContext() != null) Glide.with(getContext()).resumeRequests();
                    break;
                case SCROLL_STATE_DRAGGING://屏幕还在滚动，且手指还在屏幕上
                    if (getContext() != null) Glide.with(getContext()).pauseRequests();
                    break;
                case SCROLL_STATE_SETTLING://屏幕处于惯性滚动当中
                    if (getContext() != null) Glide.with(getContext()).pauseRequests();
                    break;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (getLayoutManager() instanceof LinearLayoutManager){
                int lastVisibleItem = ((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = AutoLoadRecyclerView.this.getAdapter().getItemCount();
                int firstVisibleItem = ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
                if (mListener != null && !isLoadMore && lastVisibleItem >= totalItemCount -2 && dy > 0){
                    mListener.loadMore();
                    isLoadMore = true;
                }
            }
        }

    }


    public static void releaseImageViewResouce(ImageView imageView) {
//        GlideApp.with(view.getContext()).clear(imageView);
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
}
