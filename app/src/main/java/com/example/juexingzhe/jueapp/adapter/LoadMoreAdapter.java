package com.example.juexingzhe.jueapp.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.juexingzhe.jueapp.bean.IBaseInfoBean;
import com.example.juexingzhe.jueapp.bean.PngInfoBean;
import com.example.juexingzhe.jueapp.util.IConstants;

import java.util.List;


public abstract class LoadMoreAdapter<T extends IBaseInfoBean> extends BaseLoadAdapter {
    private LoadMoreListener loadMoreListener;

    public LoadMoreAdapter(Context context, List<T> images) {
        super(context, images);
    }

    @Override
    public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType == IConstants.PNG_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(getImageResourceId(), parent, false);
            holder = getImageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(getVideoResourceId(), parent, false);
            holder = getVideoViewHolder(view);
            view.setTag(holder);
        }


        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void bindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getNormalItemViewType(position) == IConstants.PNG_TYPE) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            PngInfoBean bean = (PngInfoBean) list.get(position);
            imageViewHolder.imageView.setImageDrawable(context.getResources().getDrawable(Integer.parseInt(bean.getImageUrl()), null));
            imageViewHolder.imageView.setOnClickListener(v -> Toast.makeText(context, "pisition = " + position, Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    public int getNormalItemViewType(int position) {
        IBaseInfoBean bean = (IBaseInfoBean) list.get(position);
        switch (bean.getType()) {
            case IConstants.MP4_TYPE:
                return IConstants.MP4_TYPE;
            default:
                return IConstants.PNG_TYPE;
        }
    }

    @Override
    public void loadMore() {
        super.loadMore();
        if (loadMoreListener == null) return;
        loadMoreListener.loadMoreData();
    }

    /**
     * Image对应的Item Resource Id
     *
     * @return resource
     */
    public abstract int getImageResourceId();

    /**
     * Video对应的Item Resource Id
     *
     * @return resource
     */
    public abstract int getVideoResourceId();

    /**
     * Image对应的ViewHolder
     *
     * @return ViewHolder
     */
    public abstract RecyclerView.ViewHolder getImageViewHolder(View itemView);

    /**
     * Video对应的ViewHolder
     *
     * @return ViewHolder
     */
    public abstract RecyclerView.ViewHolder getVideoViewHolder(View itemView);


    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    /**
     * 加载更多数据
     */
    public interface LoadMoreListener {
        void loadMoreData();
    }
}
